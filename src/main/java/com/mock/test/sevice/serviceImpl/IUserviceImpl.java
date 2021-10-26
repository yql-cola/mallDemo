package com.mock.test.sevice.serviceImpl;

import com.mock.test.dao.UserMapper;
import com.mock.test.enums.RoleEnum;
import com.mock.test.pojo.User;
import com.mock.test.sevice.IUserService;
import com.mock.test.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.mock.test.enums.ResponseEnum.*;

@Service
public class IUserviceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVo register(User user) {

        //username cannot repeat
        int usernameCount = userMapper.countByUsername(user.getUsername());
        if (usernameCount > 0){
            return ResponseVo.error(USERNAME_EXIST);
        }
        //email cannot repeat
        int emailCount = userMapper.countByEmail(user.getEmail());
        if (emailCount > 0){
            return ResponseVo.error(EMAIL_EXIST);
        }
        //MD5加密(摘要算法)spring 自带
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        //写入数据库
        user.setRole(RoleEnum.CUSTOMER.getCode());
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0){
            return ResponseVo.error(ERROR);
        }

        return ResponseVo.success();
    }

    @Override
    public ResponseVo login(String username,String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null){
            //用户不存在
            //返回：用户名或密码错误
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword()
                .equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))){
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.success(user);

    }
}
