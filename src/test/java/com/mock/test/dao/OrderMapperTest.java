package com.mock.test.dao;

import com.mock.test.MallDemoApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrderMapperTest extends MallDemoApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void deleteByPrimaryKey() {

    }

    @Test
    void insert() {

    }

    @Test
    void insertSelective() {
    }

    @Test
    void selectByPrimaryKey() {
        System.out.println(orderMapper.selectByPrimaryKey(1).toString());
    }

    @Test
    void updateByPrimaryKeySelective() {
    }

    @Test
    void updateByPrimaryKey() {
    }
}