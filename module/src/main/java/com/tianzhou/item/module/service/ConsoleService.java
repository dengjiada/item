package com.tianzhou.item.module.service;

import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.mapper.ConsoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class ConsoleService {

    @Autowired
    private ConsoleMapper consoleMapper;

    //新增商品
    public int createItem(String coverImages, String name, BigDecimal price, String introduction) {
        //1.拿到当前的时间戳
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        //2.封装商品更新字段
        Item item = new Item().setCoverImages(coverImages)
                .setName(name)
                .setPrice(price)
                .setIntroduction(introduction)
                .setCreateTime(timeStamp).setUpdateTime(timeStamp).setIsDeleted(0);
        //3.调用mapper
        return consoleMapper.createItem(item);
    }

    //根据商品id修改商品
    public int updateItem(BigInteger id, String coverImages, String name, BigDecimal price, String introduction) {
        //1.拿到当前时间戳
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        //2.封装商品更新字段
        Item item = new Item().setId(id)
                .setCoverImages(coverImages)
                .setName(name)
                .setPrice(price)
                .setIntroduction(introduction)
                .setUpdateTime(timeStamp);
        //3.调用mapper
        return consoleMapper.updateItem(item);
    }

    //根据商品id删除商品
    public int deleteItem(BigInteger id) {
        return consoleMapper.deleteItem(id, (int) (System.currentTimeMillis() / 1000));
    }
}
