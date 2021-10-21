package com.mock.test.dao;

import com.mock.test.MallDemoApplicationTests;
import com.mock.test.pojo.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class CategoryMapperTest extends MallDemoApplicationTests {
    @Autowired
    private CategoryMapper categoryMapper;
    @Test
    void contextLoads() {
        Category byId = categoryMapper.findById(100001);
        System.out.println(byId.toString());
    }
    @Test
    public void queryById(){
        Category byId = categoryMapper.queryById(100001);
        System.out.println(byId.toString());
    }
}