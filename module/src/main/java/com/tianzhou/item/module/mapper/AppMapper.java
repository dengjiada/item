package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface AppMapper {

    //查询商品列表
    @Select("select id,cover_images,name,price from item where is_deleted = 0")
    List<Item> list();

    //根据商品id查询商品详情
    @Select("select cover_images,name,price,introduction from item where id = #{id} and is_deleted = 0")
    Item getItemInfo(@Param(value = "id") BigInteger id);
}
