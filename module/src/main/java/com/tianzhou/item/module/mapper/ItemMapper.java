package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.entity.ItemWithCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ItemMapper {
    //根据商品id查询商品详情，需要判断逻辑删除标记（is_deleted）
    @Select("select * from item where id = #{id} and is_deleted = 0")
    Item getById(@Param(value = "id") Long id);

    //根据商品id查询商品详情，不需要判断逻辑删除标记（is_deleted）
    @Select("select * from item where id = #{id}")
    Item extractById(@Param(value = "id") Long id);

    //新增商品
    int insert(@Param(value = "item") Item item);

    //根据商品id修改商品信息
    int update(@Param(value = "item") Item item);

    //根据商品id删除商品
    @Update("update item set is_deleted = 1,update_time = #{timeStamp} where id = #{id} and is_deleted = 0 limit 1")
    int delete(@Param(value = "id") Long id, @Param(value = "timeStamp") int timeStamp);

    //根据分页参数查询分页数据
    List<Item> selectItemPage(@Param(value = "offset") int offset, @Param(value = "pageSize") int pageSize, @Param(value = "keyword") String keyword);

    //联表根据分页参数查询分页数据
    List<ItemWithCategory> selectItemWithCategoryPage(@Param(value = "offset") int offset, @Param(value = "pageSize") int pageSize, @Param(value = "keyword") String keyword);

    //查询商品总条数
    Long countItemTotal(@Param(value = "keyword") String keyword);

    //根据分类id查询商品
    @Select("select * from item where category_id = #{categoryId} and is_deleted = 0")
    List<Item> getItemByCategoryId(Long categoryId);
}
