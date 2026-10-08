package com.tianzhou.item.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 用户表 实体
 * @author test
 * @since 2026-10-06
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "password")
    private String password;

    @TableField(value = "salt")
    private String salt;

    @TableField(value = "name")
    private String name;

    @TableField(value = "avatar")
    private String avatar;

    @TableField(value = "create_time")
    private Integer createTime;

    @TableField(value = "update_time")
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000L);

    @TableField(value = "is_deleted")
    private Integer isDeleted;

}