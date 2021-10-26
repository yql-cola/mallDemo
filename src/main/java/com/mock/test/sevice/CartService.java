package com.mock.test.sevice;

import com.mock.test.form.CartAddForm;
import com.mock.test.form.CartUpdateForm;
import com.mock.test.vo.CartVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    ResponseVo<CartVo> add(Integer uid,CartAddForm form);

    ResponseVo<CartVo> list(Integer uid);

    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form);

    ResponseVo<CartVo> delete(Integer uid, Integer productId);

    ResponseVo<CartVo> selectAll(Integer uid);

    ResponseVo<CartVo> unSelectAll(Integer uid);

    ResponseVo<Integer> sum(Integer uid);

}
