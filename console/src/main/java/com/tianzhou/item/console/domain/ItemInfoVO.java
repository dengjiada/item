package com.tianzhou.item.console.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoVO {
    //轮播图
    private List<String> coverImages;
    //商品名字
    private String name;
    //商品价格
    private Float price;
    //商品介绍
    private String introduction;
    //商品分类名
    private String categoryName;
    //商品分类图
    private String categoryImage;
    //商品创建时间
    private String createTime;
    //商品修改时间
    private String updateTime;
}
