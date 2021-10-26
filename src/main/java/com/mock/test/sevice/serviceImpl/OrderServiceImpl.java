package com.mock.test.sevice.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mock.test.dao.OrderItemMapper;
import com.mock.test.dao.OrderMapper;
import com.mock.test.dao.ProductMapper;
import com.mock.test.dao.ShippingMapper;
import com.mock.test.enums.OrderStatusEnum;
import com.mock.test.enums.PaymentTypeEnum;
import com.mock.test.enums.ProductStatusEnum;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.pojo.*;
import com.mock.test.sevice.OrderService;
import com.mock.test.vo.OrderItemVo;
import com.mock.test.vo.OrderVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private CartServiceImpl cartService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        //收货地址校验
        Shipping shipping = shippingMapper.selectByUidAndShippingId(shippingId, uid);
        if (shipping == null){
            return ResponseVo.error(ResponseEnum.SHIPPING_FAIL);
        }
        //获取购物车，校验(是否有商品，库存)
        List<Cart> carts = cartService.listForCart(uid).stream().filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(carts)){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }
        //获取cartList里的productId
        Set<Integer> productIds = carts.stream().map(Cart::getProductId).collect(Collectors.toSet());
        List<Product> productList = productMapper.selectProductIdSet(productIds);
        if (CollectionUtils.isEmpty(productList)){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        Map<Integer,Product> map = productList.stream().collect(Collectors.toMap(Product::getId,product -> product));
        Long orderNo = generateOrderNo();
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : carts) {
            //根据productId查数据库
            Product product = map.get(cart.getProductId());
            //是否有商品
            if (product == null){
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,"商品不存在，productId = "+cart.getProductId());
            }
            //商品上下架状态
            if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE,ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc()+product.getName());
            }
            //库存
            if (product.getStock() < cart.getQuantity()){
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,product.getName()+"库存不正确");
            }

            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItems.add(orderItem);
            product.setStock(product.getStock()-cart.getQuantity());
            //减库存
            int row = productMapper.updateByPrimaryKeySelective(product);
            if (row <= 0){
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }
        //计算总价，只计算选中的商品
        //生成订单，入库：order和order_item
        Order order = buildOrder(uid, orderNo, shippingId, orderItems);
        int row = orderMapper.insertSelective(order);
        if (row <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        int row1 = orderItemMapper.batchInsert(orderItems);
        if (row1 <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        //更新购物车
        //Redis有事务(打包命令)，不能回滚
        for (Cart cart : carts) {
            cartService.delete(uid,cart.getProductId());
        }
        //构造orderVo
        OrderVo orderVo = buildOrderVo(order, orderItems, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.selectByUid(uid);
        Set<Long> set = orders.stream().map(Order::getOrderNo).collect(Collectors.toSet());
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoSet(set);
        Map<Long,List<OrderItem>> map = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));
        Set<Integer> shippingIdSet = orders.stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        List<Shipping> shippings = shippingMapper.selectByShippingIdSet(shippingIdSet);
        Map<Integer,Shipping> shippingMap = shippings.stream()
                .collect(Collectors.toMap(Shipping::getId,e->e));
        List<OrderVo> orderVos = new ArrayList<>();
        for (Order order : orders) {
            orderVos.add(buildOrderVo(order,map.get(order.getOrderNo()),shippingMap.get(order.getShippingId())));
        }
        PageInfo pageInfo = new PageInfo(orders);
        pageInfo.setList(orderVos);

        return ResponseVo.success(pageInfo);




    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        Set<Long> set = new HashSet<>();
        set.add(orderNo);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoSet(set);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = buildOrderVo(order, orderItems, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        //只有未付款订单才可取消
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc() + ":订单id:"+orderNo);
        }
        //只有未付款订单才可变成已付款
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc() + ":订单id:"+orderNo);
        }
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            throw new RuntimeException(ResponseEnum.ERROR.getDesc());
        }
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItems, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        List<OrderItemVo> OrderItemVoList = orderItems.stream().map(e -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(e, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());
        orderVo.setOrderItemList(OrderItemVoList);
        if (shipping != null) {
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }
        return orderVo;
    }

    /**
     * 企业级：分布式唯一id/主键
     * @return
     */
    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList
    ) {
        BigDecimal payment = orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }

    private OrderItem buildOrderItem(Integer uid,Long orderNo,Integer quantity,Product product ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(uid);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setProductName(product.getName());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }
}
