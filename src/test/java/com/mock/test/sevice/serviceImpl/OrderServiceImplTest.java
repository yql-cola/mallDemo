package com.mock.test.sevice.serviceImpl;

import com.github.pagehelper.PageInfo;
import com.mock.test.MallDemoApplicationTests;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.sevice.OrderService;
import com.mock.test.vo.OrderVo;
import com.mock.test.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Slf4j
@Transactional
public class OrderServiceImplTest extends MallDemoApplicationTests {

    @Autowired
    private OrderService orderService;

    private Integer uid = 1;

    private Integer shippingId = 4;
    private ResponseVo<OrderVo> create1() {
        ResponseVo<OrderVo> orderVoResponseVo = orderService.create(uid, shippingId);

        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),orderVoResponseVo.getCode());
        return orderVoResponseVo;
    }
    @Test
    public void create() {
        ResponseVo<OrderVo> orderVoResponseVo = orderService.create(uid, shippingId);

        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),orderVoResponseVo.getCode());
    }

    @Test
    public void list(){
        ResponseVo<PageInfo> orderVoResponseVo = orderService.list(uid, 1,10);
        log.info("list查询结束，list = {}",orderVoResponseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),orderVoResponseVo.getCode());
    }
    @Test
    public void detail(){
        ResponseVo<OrderVo> detail = orderService.detail(uid, create1().getData().getOrderNo());
        log.info("detail查询结束，list = {}",detail);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),detail.getCode());
    }
    @Test
    public void cancel(){
        ResponseVo<OrderVo> detail = orderService.cancel(uid, create1().getData().getOrderNo());
        log.info("detail查询结束，list = {}",detail);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),detail.getCode());
    }
}