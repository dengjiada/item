package com.tianzhou.item.module.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithCategory {
    //商品id
    private Long itemId;
    //商品轮播图，用$拼接
    private String coverImages;
    //商品名字
    private String itemName;
    //商品价格
    private BigDecimal price;
    //商品介绍
    private String introduction;
    //商品创建时间
    private Integer itemCreateTime;
    //商品修改时间
    private Integer itemUpdateTime = (int) (System.currentTimeMillis() / 1000L);
    //商品是否已经被删除
    private Integer itemIsDeleted;
    //商品分类id
    private Long categoryId;
    //商品分类名
    private String categoryName;
    //商品分类图
    private String image;
    //商品分类创建时间
    private Integer categoryCreateTime;
    //商品分类修改时间
    private Integer categoryUpdateTime = (int) (System.currentTimeMillis() / 1000L);
    //商品分类是否被逻辑删除
    private Integer categoryIsDeleted;
}
