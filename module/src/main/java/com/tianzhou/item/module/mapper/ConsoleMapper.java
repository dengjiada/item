package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;

@Mapper
public interface ConsoleMapper {
    //新增商品
    int createItem(@Param(value = "item") Item item);

    //根据商品id修改商品信息
    int updateItem(@Param(value = "item") Item item);

    //根据商品id删除商品
    @Update("update item set is_deleted = 1,update_time = #{timeStamp} where id = #{id} limit 1")
    int deleteItem(@Param(value = "id") BigInteger id, @Param(value = "timeStamp") int timeStamp);
}
