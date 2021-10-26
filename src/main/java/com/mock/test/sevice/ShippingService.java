package com.mock.test.sevice;

import com.github.pagehelper.PageInfo;
import com.mock.test.form.ShippingForm;
import com.mock.test.vo.ResponseVo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ShippingService {

    ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm from);
    ResponseVo delete(Integer uid, Integer shippingId);
    ResponseVo update(Integer uid, Integer shippingId,ShippingForm from);
    ResponseVo<PageInfo> list(Integer uid, Integer pageNum,Integer pageSize);




}
