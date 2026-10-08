package com.tianzhou.item.module.mapper;

import com.tianzhou.item.module.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author test
 * @since 2026-10-06
 */
@Mapper
public interface UserMapper {
    //根据手机号查询用户
    @Select("select * from user where phone = #{phone} and is_deleted = 0")
    User getUserByPhone(String phone);

    //根据id查询详情，需要判断逻辑删除标记（is_deleted）
    @Select("select * from user where id = #{id} and is_deleted = 0")
    User getById(@Param(value = "id") Long id);

    //根据id查询详情，不需要判断逻辑删除标记（is_deleted）
    @Select("select * from user where id = #{id}")
    User extractById(@Param(value = "id") Long id);

    //新增
    int insert(@Param(value = "entity") User entity);

    //修改
    int update(@Param(value = "entity") User entity);

    //根据id逻辑删除
    @Update("update user set is_deleted = 1,update_time = #{timeStamp} where id = #{id} and is_deleted = 0 limit 1")
    int delete(@Param(value = "id") Long id, @Param(value = "timeStamp") int timeStamp);
}
