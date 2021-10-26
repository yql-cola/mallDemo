package com.mock.test.sevice.serviceImpl;

import com.mock.test.MallDemoApplicationTests;
import com.mock.test.enums.ResponseEnum;
import com.mock.test.enums.RoleEnum;
import com.mock.test.pojo.User;
import com.mock.test.sevice.IUserService;
import com.mock.test.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class IUserviceImplTest extends MallDemoApplicationTests {


    public static final String USERNAME = "jack";

    public static final String PASSWORD = "123456";

    @Autowired
    private IUserService userService;

    @Before
    public void register() {
        User user = new User(USERNAME, PASSWORD, "jack@qq.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }

    @Test
    public void login() {
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getCode());
    }
}