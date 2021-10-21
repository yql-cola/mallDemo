package com.mock.test.dao;

import com.mock.test.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 开发生成器: 连接数据库 -> 获取表结构 -> 生成文件
 * Created by 廖师兄
 * 2035-01-18 18:25
 */
//@Mapper
public interface CategoryMapper {

	@Select("select * from mall_category where id = #{id}")
	Category findById(@Param("id") Integer id);

	Category queryById(Integer id);
}
