package com.tianzhou.item.app.domain;

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
    //轮播图数组
    private List<String> coverImages;
    //商品名字
    private String name;
    //商品价格
    private Float price;
    //商品介绍
    private String introduction;
}
