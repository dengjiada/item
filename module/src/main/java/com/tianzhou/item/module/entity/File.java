package com.tianzhou.item.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文件类型表 实体
 *
 * @author test
 * @since 2026-09-19
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("file")
public class File {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "type")
    private Integer type;

    @TableField(value = "original_name")
    private String originalName;

    @TableField(value = "object_name")
    private String objectName;

    @TableField(value = "url")
    private String url;

    @TableField(value = "create_time")
    private Integer createTime;

    @TableField(value = "update_time")
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000L);

    @TableField(value = "is_deleted")
    private Byte isDeleted;

}