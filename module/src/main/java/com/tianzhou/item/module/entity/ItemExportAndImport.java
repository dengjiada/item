package com.tianzhou.item.module.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemExportAndImport {
    @ExcelProperty(value = "封面图", index = 0)
    private String coverImages;
    @ExcelProperty(value = "商品名", index = 1)
    private String name;
    @ExcelProperty(value = "商品价格", index = 2)
    private BigDecimal price;
    @ExcelProperty(value = "商品介绍", index = 3)
    private String introduction;
    @ExcelProperty(value = "创建时间", index = 4)
    private Integer createTime;
    @ExcelProperty(value = "修改时间", index = 5)
    private Integer updateTime;
    @ExcelProperty(value = "是否删除", index = 6)
    private Integer isDeleted;
    @ExcelProperty(value = "分类id", index = 7)
    private Long categoryId;
}
