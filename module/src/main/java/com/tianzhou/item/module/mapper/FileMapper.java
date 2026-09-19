package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 文件类型表 Mapper 接口
 * </p>
 *
 * @author test
 * @since 2026-09-19
 */
@Mapper
public interface FileMapper {

    //根据id查询详情，需要判断逻辑删除标记（is_deleted）
    @Select("select * from file where id = #{id} and is_deleted = 0")
    File getById(@Param(value = "id") Long id);

    //根据id查询详情，不需要判断逻辑删除标记（is_deleted）
    @Select("select * from file where id = #{id}")
    File extractById(@Param(value = "id") Long id);

    //新增
    int insert(@Param(value = "entity") File entity);

    //修改
    int update(@Param(value = "entity") File entity);

    //根据id逻辑删除
    @Update("update file set is_deleted = 1,update_time = #{timeStamp} where id = #{id} and is_deleted = 0 limit 1")
    int delete(@Param(value = "id") Long id, @Param(value = "timeStamp") int timeStamp);
}
