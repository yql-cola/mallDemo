package com.mock.test.controller;

import com.mock.test.constant.MallConstant;
import com.mock.test.form.ShippingForm;
import com.mock.test.pojo.User;
import com.mock.test.sevice.ShippingService;
import com.mock.test.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping("/add")
    public ResponseVo add(@Valid @RequestBody ShippingForm form, HttpSession session){
        User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
        return shippingService.add(user.getId(),form);
    }

    @DeleteMapping("/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId, HttpSession session){
        User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
        return shippingService.delete(user.getId(),shippingId);
    }
    @PutMapping("/{shippingId}")
    public ResponseVo updata(@PathVariable Integer shippingId,@Valid @RequestBody ShippingForm form,HttpSession session){
        User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
        return shippingService.update(user.getId(),shippingId,form);
    }
    @GetMapping("/shippings")
    public ResponseVo list(@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                           @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                           HttpSession session){
        User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
        return shippingService.list(user.getId(),pageNum,pageSize);
    }

}
