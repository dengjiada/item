package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 商品分类表 Mapper 接口
 * </p>
 *
 * @author test
 * @since 2026-08-29
 */
@Mapper
public interface CategoryMapper {

    //根据id查询分类详情，需要判断逻辑删除标记（is_deleted）
    @Select("select * from category where id = #{id} and is_deleted = 0")
    Category getById(@Param(value = "id") Long id);

    //根据id查询分类详情，不需要判断逻辑删除标记（is_deleted）
    @Select("select * from category where id = #{id}")
    Category extractById(@Param(value = "id") Long id);

    //新增分类
    int insert(@Param(value = "entity") Category entity);

    //修改分类
    int update(@Param(value = "entity") Category entity);

    //根据id逻辑删除分类
    @Update("update category set is_deleted = 1,update_time = #{timeStamp} where id = #{id} and is_deleted = 0 limit 1")
    int delete(@Param(value = "id") Long id, @Param(value = "timeStamp") int timeStamp);

    //查询分类列表
    @Select("select * from category where is_deleted = 0")
    List<Category> list();

    //根据关键词查询符合的商品id
    List<Long> selectCategoryIdsByCategoryName(String keyword);
}
