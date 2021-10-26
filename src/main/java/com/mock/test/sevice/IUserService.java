package com.mock.test.sevice;

import com.mock.test.pojo.User;
import com.mock.test.vo.ResponseVo;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    /**
     * 注册
     * @return
     */
    ResponseVo<User> register(User user);
    /**
     * login
     */
    ResponseVo<User> login(String username,String password);

}
