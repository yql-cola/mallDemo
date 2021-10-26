package com.mock.test.sevice;

import com.github.pagehelper.PageInfo;
import com.mock.test.vo.ProductDetailVo;
import com.mock.test.vo.ProductVo;
import com.mock.test.vo.ResponseVo;

public interface ProductService {

    ResponseVo<PageInfo<ProductVo>> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}
