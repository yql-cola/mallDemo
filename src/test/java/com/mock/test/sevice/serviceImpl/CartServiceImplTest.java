package com.mock.test.sevice.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mock.test.MallDemoApplicationTests;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.form.CartAddForm;
import com.mock.test.form.CartUpdateForm;
import com.mock.test.sevice.CartService;
import com.mock.test.vo.CartVo;
import com.mock.test.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CartServiceImplTest extends MallDemoApplicationTests {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private CartService cartService;


    private Integer uid = 1;

    private Integer productId = 26;

    @Test
    public void add() {
        CartAddForm form = new CartAddForm();
        form.setProductId(27);
        form.setSelected(true);
        cartService.add(1,form);
    }
    @Test
    public void list() {
        ResponseVo<CartVo> list = cartService.list(1);
        log.info("list={}",gson.toJson(list));

    }

    @Test
    public void update() {
        ResponseVo<CartVo> list = cartService.update(1,26,new CartUpdateForm(5,false));
        log.info("update={}",gson.toJson(list));

    }


    @Test
    public void delect() {
        ResponseVo<CartVo> list = cartService.delete(1,26);
        log.info("update={}",gson.toJson(list));
    }

    @Test
    public void selectAll() {
        ResponseVo<CartVo> responseVo = cartService.selectAll(uid);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }

    @Test
    public void unSelectAll() {
        ResponseVo<CartVo> responseVo = cartService.unSelectAll(uid);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }

    @Test
    public void sum() {
        ResponseVo<Integer> responseVo = cartService.sum(uid);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }
}