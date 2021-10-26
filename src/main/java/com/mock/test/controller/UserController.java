package com.mock.test.controller;


import com.mock.test.enums.ResponseEnum;
import com.mock.test.form.UserLoginForm;
import com.mock.test.form.UserRegisterForm;
import com.mock.test.pojo.User;
import com.mock.test.sevice.IUserService;
import com.mock.test.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.mock.test.constant.MallConstant.CURRENT_USER;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userSevice;

    @PostMapping("/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseVo.error(ResponseEnum.PARAM_ERROR,bindingResult);
        }
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);

        return userSevice.register(user);
    }

    @PostMapping("/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  BindingResult bindingResult,
                                  HttpSession session){
        if (bindingResult.hasErrors()){
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR,bindingResult);
        }

        ResponseVo<User> userResponseVo = userSevice.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        //设置session
        session.setAttribute(CURRENT_USER,userResponseVo.getData());

        return userResponseVo;
    }

    @GetMapping
    public ResponseVo<User> userInfo(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null){
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }

        return ResponseVo.success(user);
    }

    //TODO 判断登录状态，拦截器
    @PostMapping("/logout")
    public ResponseVo logout(HttpSession session){
        ResponseVo<User> info = userInfo(session);
        if (info.getCode().equals(ResponseEnum.NEED_LOGIN.getCode()) ){
            return info;
        }
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();

    }

}
