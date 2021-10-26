package com.mock.test.sevice;

import com.mock.test.vo.CategoryVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface CategoryService {

    ResponseVo<List<CategoryVo>> selectAll();

    void findSubCategoryId(Integer id, Set<Integer> resultSet);

}
