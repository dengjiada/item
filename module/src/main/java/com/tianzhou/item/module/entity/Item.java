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
public class Item {
    //商品id
    private Long id;
    //商品轮播图，用$拼接
    private String coverImages;
    //商品名字
    private String name;
    //商品价格
    private BigDecimal price;
    //商品介绍
    private String introduction;
    //创建时间
    private Integer createTime;
    //修改时间
    private Integer updateTime;
    //是否已经被删除
    private Integer isDeleted;

    //为了确保insert/update操作一定成功，在Item类中提供一个静态方法，给updateTime默认值
    public static Item create() {
        //拿到当前时间戳
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Item item = new Item();
        item.setUpdateTime(timeStamp);
        return item;
    }
}
