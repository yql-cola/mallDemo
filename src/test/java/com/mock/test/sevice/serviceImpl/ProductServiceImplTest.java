package com.mock.test.sevice.serviceImpl;

import com.mock.test.MallDemoApplicationTests;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.sevice.ProductService;
import com.mock.test.vo.ProductDetailVo;
import com.mock.test.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceImplTest extends MallDemoApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    public void testList() {
        productService.list(null,1,2);
    }

    @Test
    public void detail() {
        ResponseVo<ProductDetailVo> responseVo = productService.detail(26);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }
}