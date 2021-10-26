package com.mock.test.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Integer id;
    private Integer parentId;
    private String name;
    private Integer sortOrder;
    private List<CategoryVo> subCategories;

    public CategoryVo() {
    }

    public CategoryVo(Integer id, Integer parentId, String name,Integer sortOrder) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.sortOrder = sortOrder;
    }
}
