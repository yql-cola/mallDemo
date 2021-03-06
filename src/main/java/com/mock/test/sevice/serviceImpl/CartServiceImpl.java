package com.mock.test.sevice.serviceImpl;

import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.mock.test.dao.ProductMapper;
import com.mock.test.enums.ProductStatusEnum;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.form.CartAddForm;
import com.mock.test.form.CartUpdateForm;
import com.mock.test.pojo.Cart;
import com.mock.test.pojo.Product;
import com.mock.test.sevice.CartService;
import com.mock.test.vo.CartProductVo;
import com.mock.test.vo.CartVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";
    Gson gson = new Gson();
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        CartVo cartVo = new CartVo();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Map<String, String> entries = opsForHash.entries(redisKey);
        Set<Integer> set = new HashSet();
        List<Cart> carts = new ArrayList<>();

        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            if (!ObjectUtils.isEmpty(cart)&&!cart.getProductSelected()){
                selectAll = false;
            }
            set.add(productId);
            carts.add(cart);
        }
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        if (!set.isEmpty()){
            List<Product> productList = productMapper.selectProductIdSet(set);
            Map<Integer,Product> map = productList.stream().collect(Collectors.toMap(Product::getId,product -> product));
            for (Cart cart : carts) {
                Product product = map.get(cart.getProductId());
                //???????????????
                if (product == null){
                    return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,"??????????????????productId = "+cart.getProductId());
                }
                //?????????????????????
                if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
                    return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE,ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc()+product.getName());
                }
                if (product.getId().equals(cart.getProductId())){
                    cartTotalQuantity += cart.getQuantity();
                    CartProductVo cartProductVo = new CartProductVo(product.getId(),
                            cart.getQuantity(),
                            product.getName(),
                            product.getSubtitle(),
                            product.getMainImage(),
                            product.getPrice(),
                            product.getStatus(),
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            product.getStock(), cart.getProductSelected());
                    cartProductVoList.add(cartProductVo);
                    //?????????????????????????????????
                    if (cart.getProductSelected()){
                        cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                    }
                }
            }

        }
        //???????????????????????????????????????
        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> add(Integer uid,CartAddForm form) {

        Integer quantity = 1;

        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        //??????????????????
        if (product == null){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }

        //????????????????????????
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }

        //????????????????????????
        if (product.getStock()<=0){
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }

        //??????redis
        //key cart_key
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        Cart cart;
        if (StringUtil.isEmpty(value)){
            //??????????????? ??????
            cart = new Cart(product.getId(), quantity, form.getSelected());
        }else {
            //???????????? ??????+1
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
        }

        opsForHash.put(redisKey,
                String.valueOf(product.getId()),
                gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtil.isEmpty(value)){
            //??????????????? ??????
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        //???????????? ????????????
        Cart cart = gson.fromJson(value, Cart.class);
        if (form.getQuantity() != null && form.getQuantity() >= 0){
            cart.setQuantity(form.getQuantity());

        }
        if (form.getSelected() != null){
            cart.setProductSelected(form.getSelected());
        }

        opsForHash.put(redisKey,String.valueOf(productId),gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtil.isEmpty(value)){
            //??????????????? ??????
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        //???????????? ??????
        opsForHash.delete(redisKey,String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        List<Cart> carts = listForCart(uid);
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        for (Cart cart : carts) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));
            
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        List<Cart> carts = listForCart(uid);
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        for (Cart cart : carts) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));

        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream()
                .map(Cart::getQuantity)
                .reduce(0, Integer::sum);
        return ResponseVo.success(sum);
    }

    public List<Cart> listForCart(Integer uid){
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> carts = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            carts.add(gson.fromJson(entry.getValue(),Cart.class));
        }
        return carts;
    }
}
