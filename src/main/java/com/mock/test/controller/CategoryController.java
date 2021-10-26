package com.mock.test.controller;

import com.mock.test.sevice.CategoryService;
import com.mock.test.vo.CategoryVo;
import com.mock.test.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> select(){
        return categoryService.selectAll();
    }


}
