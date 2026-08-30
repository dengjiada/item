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
 * 商品分类表 实体
 * @author test
 * @since 2026-08-29
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("category")
public class Category {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "image")
    private String image;

    @TableField(value = "create_time")
    private Integer createTime;

    @TableField(value = "update_time")
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000L);

    @TableField(value = "is_deleted")
    private Byte isDeleted;

}