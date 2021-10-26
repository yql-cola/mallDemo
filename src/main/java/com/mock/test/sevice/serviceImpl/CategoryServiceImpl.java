package com.mock.test.sevice.serviceImpl;

import com.mock.test.constant.MallConstant;
import com.mock.test.dao.CategoryMapper;
import com.mock.test.pojo.Category;
import com.mock.test.sevice.CategoryService;
import com.mock.test.vo.CategoryVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List<Category> categories = categoryMapper.selectAll();
        //查出parent_id = 0
        List<CategoryVo> parentList = categories.stream()
                .filter(s->s.getParentId().equals(MallConstant.ROOT_PARENT_ID))
                .map(e->new CategoryVo(e.getId(),e.getParentId(),e.getName(),e.getSortOrder()))
                .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect(Collectors.toList());
        //查询子目录
        findSubCategories(parentList,categories);


        return ResponseVo.success(parentList);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, resultSet,categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(), resultSet,categories);
            }
        }
    }


    private void findSubCategories(List<CategoryVo> parentList,List<Category> categories){
        for (CategoryVo categoryVo : parentList) {
            List<CategoryVo> subCategoryVoList = new ArrayList<>();
            for (Category category : categories) {
                //如果查到内容，设置subCategory，继续往下查
                if(category.getParentId().equals(categoryVo.getId())){
                    subCategoryVoList.add(new CategoryVo(category.getId(),category.getParentId(),category.getName(),category.getSortOrder()));
                }

                findSubCategories(subCategoryVoList,categories);
            }
            if (subCategoryVoList.size() > 1) {
                subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
            }
//            subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
            categoryVo.setSubCategories(subCategoryVoList);
        }
    }
}
