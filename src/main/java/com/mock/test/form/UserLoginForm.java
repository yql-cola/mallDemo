package com.mock.test.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginForm {

    //    @NotNull
//    @NotEmpty //用于集合
    @NotBlank(message = "用户名不能为空")//用于String 判断空格
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

}
