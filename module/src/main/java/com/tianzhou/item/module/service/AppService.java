package com.tianzhou.item.module.service;

import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.mapper.AppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class AppService {

    @Autowired
    private AppMapper appMapper;

    //查询商品列表
    public List<Item> list() {
        return appMapper.list();
    }

    //根据商品id查询商品详情
    public Item getItemInfo(BigInteger id) {
        return appMapper.getItemInfo(id);
    }
}
