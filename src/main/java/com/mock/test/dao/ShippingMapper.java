package com.mock.test.dao;

import com.mock.test.pojo.Shipping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);
    int deleteByIdAndUid(@Param("id") Integer id, @Param("uid") Integer uid);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);
    Shipping selectByUidAndShippingId(@Param("id") Integer shippingId, @Param("uid") Integer uid);
    List<Shipping> selectByUid(Integer uid);


    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    List<Shipping> selectByShippingIdSet(@Param("shippingIdSet") Set<Integer> shippingIdSet);
}