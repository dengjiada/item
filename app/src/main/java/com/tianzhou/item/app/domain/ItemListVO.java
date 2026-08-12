package com.tianzhou.item.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemListVO {
    //商品id
    private BigInteger itemId;
    //商品列表图（轮播图的第一张图）
    private String wallImage;
    //商品名字
    private String name;
    //商品价格
    private BigDecimal price;
}
