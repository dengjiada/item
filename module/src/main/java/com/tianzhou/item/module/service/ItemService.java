package com.tianzhou.item.module.service;

import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;

    //查询商品列表
    public List<Item> list() {
        return itemMapper.list();
    }

    //根据商品id查询商品详情
    public Item getItemInfo(Long id) {
        return itemMapper.getItemInfo(id);
    }

    //新增商品
    public Long createItem(String coverImages, String name, Float price, String introduction) {
        //1.拿到当前的时间戳
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        //2.封装商品更新字段
        Item item = Item.create().setCoverImages(coverImages)
                .setName(name)
                //Float转成BigDecimal
                .setPrice(BigDecimal.valueOf(price))
                .setIntroduction(introduction)
                .setCreateTime(timeStamp)
                .setIsDeleted(0);
        //3.调用mapper
        itemMapper.createItem(item);
        return item.getId();
    }

    //根据商品id修改商品
    public int updateItem(Long id, String coverImages, String name, Float price, String introduction) {
        //1.封装商品更新字段
        Item item = Item.create().setId(id)
                .setCoverImages(coverImages)
                .setName(name)
                //Float转成BigDecimal
                .setPrice(BigDecimal.valueOf(price))
                .setIntroduction(introduction);
        //2.调用mapper
        return itemMapper.updateItem(item);
    }

    //根据商品id删除商品
    public int deleteItem(Long id) {
        return itemMapper.deleteItem(id, (int) (System.currentTimeMillis() / 1000));
    }
}
