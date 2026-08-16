package com.tianzhou.item.console.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ItemListVO {
    //商品id
    private Long itemId;
    //商品列表图（轮播图的第一张图）
    private String wallImage;
    //商品名字
    private String name;
    //商品价格
    private Float price;
}
