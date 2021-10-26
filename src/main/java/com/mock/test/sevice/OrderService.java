package com.mock.test.sevice;

import com.github.pagehelper.PageInfo;
import com.mock.test.vo.OrderVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    ResponseVo<OrderVo> create(Integer uid,Integer shipping);

    ResponseVo<PageInfo> list(Integer uid,Integer pageNum,Integer pageSize);

    ResponseVo<OrderVo> detail(Integer uid,Long orderNo);
    ResponseVo cancel(Integer uid,Long orderNo);

    void paid(Long orderNo);
}
