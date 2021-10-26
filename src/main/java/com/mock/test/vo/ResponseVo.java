package com.mock.test.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mock.test.enums.ResponseEnum;
import lombok.Data;
import org.springframework.validation.BindingResult;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseVo<T> {
    private Integer code;
    private String msg;
    private T data;

    private ResponseVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ResponseVo(Integer code,T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> ResponseVo<T> successByMsg(String msg){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), msg);
    }
    public static <T> ResponseVo<T> success(){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getDesc());
    }
    public static <T> ResponseVo<T> success(T data){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(),data );
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum){
        return new ResponseVo<>(responseEnum.getCode(),responseEnum.getDesc());
    }
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum,String msg){
        return new ResponseVo<>(responseEnum.getCode(),msg);
    }
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, BindingResult bindingResult){
        return new ResponseVo<>(responseEnum.getCode(),bindingResult.getFieldError().getField()+":"+bindingResult.getFieldError().getDefaultMessage());
    }

}
