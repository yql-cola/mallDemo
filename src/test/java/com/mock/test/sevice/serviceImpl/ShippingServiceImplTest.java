package com.mock.test.sevice.serviceImpl;

import com.mock.test.MallDemoApplicationTests;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.form.ShippingForm;
import com.mock.test.sevice.ShippingService;
import com.mock.test.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
public class ShippingServiceImplTest extends MallDemoApplicationTests {
    @Autowired
    private ShippingService shippingService;

    private Integer uid = 1;

    private ShippingForm form;

    private Integer shippingId;

    @Before
    public void before() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("廖师兄");
        form.setReceiverAddress("慕课网");
        form.setReceiverCity("北京");
        form.setReceiverMobile("18812345678");
        form.setReceiverPhone("010123456");
        form.setReceiverProvince("北京");
        form.setReceiverDistrict("海淀区");
        form.setReceiverZip("000000");
        this.form = form;

//        add();
    }
    @Test
    public void add() {
        ResponseVo<Map<String, Integer>> responseVo = shippingService.add(uid, form);
        log.info("result={}", responseVo);
        this.shippingId = responseVo.getData().get("shippingId");
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }

//    @After
    public void delete() {
        ResponseVo responseVo = shippingService.delete(uid, shippingId);
        log.info("result={}", responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }

    @Test
    public void update() {
        form.setReceiverCity("杭州");
        ResponseVo responseVo = shippingService.update(uid, shippingId, form);
        log.info("result={}", responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }

    @Test
    public void list() {
        ResponseVo responseVo = shippingService.list(uid, 1, 10);
        log.info("result={}", responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }
}