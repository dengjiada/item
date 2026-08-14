package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ItemMapper {
    //查询商品列表
    @Select("select id,cover_images,name,price from item where is_deleted = 0")
    List<Item> list();

    //根据商品id查询商品详情
    @Select("select cover_images,name,price,introduction from item where id = #{id} and is_deleted = 0")
    Item getItemInfo(@Param(value = "id") Long id);

    //新增商品
    int createItem(@Param(value = "item") Item item);

    //根据商品id修改商品信息
    int updateItem(@Param(value = "item") Item item);

    //根据商品id删除商品
    @Update("update item set is_deleted = 1,update_time = #{timeStamp} where id = #{id} limit 1")
    int deleteItem(@Param(value = "id") Long id, @Param(value = "timeStamp") int timeStamp);
}
