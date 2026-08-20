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

    //将insert，update合成成edit
    public Long edit(Long id, String coverImages, String name, Float price, String introduction) {
        //1. 校验参数
        //1.1 校验coverImages
        if (coverImages == null || coverImages.trim().isEmpty()) {
            throw new RuntimeException("coverImages cannot be empty");
        }
        //1.2 校验name
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("name cannot be empty");
        }
        if (name.length() > 50) {
            throw new RuntimeException("name length cannot exceed 50");
        }
        //1.3 校验price
        if (price == null) {
            throw new RuntimeException("price cannot be null");
        }
        if (price < 0) {
            throw new RuntimeException("price cannot be negative");
        }
        //1.4 校验introduction
        if (introduction == null) {
            throw new RuntimeException("introduction cannot be null");
        }
        if (introduction.length() > 2000) {
            throw new RuntimeException("introduction length cannot exceed 2000");
        }

        //2.创建item对象
        Item item = Item.create().setCoverImages(coverImages)
                .setName(name)
                .setPrice(BigDecimal.valueOf(price))
                .setIntroduction(introduction);

        //3. 根据id是否为null来决定进入的是insert分支还是update分支
        //3.1 id为null，进入insert分支
        if (id == null) {
            //3.1.1 为insert的对象添加剩余的值
            item.setCreateTime((int) (System.currentTimeMillis() / 1000)).setIsDeleted(0);
            //3.1.2 调用mapper方法
            int rows = itemMapper.createItem(item);
            //3.1.3 如果rows=0，认为insert失败，抛异常
            if (rows == 0) {
                throw new RuntimeException("create item failed");
            }
            //3.1.4 insert成功，返回insert生成的id
            return item.getId();
        }
        //3.2 id不为null，进入update分支
        //3.2.1 校验id
        if (id <= 0) {
            throw new RuntimeException("id must greater than 0");
        }
        //调用mapper查询id对应的entity是否在数据库中
        Item item1 = itemMapper.selectItemById(id);
        if (item1 == null) {
            throw new RuntimeException("item id not exist");
        }
        //3.2.2 为update的对象添加剩余的值
        item.setId(id);
        //3.2.3 调用mapper方法
        int rows = itemMapper.updateItem(item);
        //3.2.4 如果rows=0，认为update失败，抛异常
        if (rows == 0) {
            throw new RuntimeException("update item failed");
        }
        //3.2.5 update成功，返回Item的id
        return id;
    }

    //根据商品id删除商品
    public int deleteItem(Long id) {
        return itemMapper.deleteItem(id, (int) (System.currentTimeMillis() / 1000));
    }

    //查询商品列表分页数据
    public List<Item> selectItemPage(int page, int pageSize, String keyword) {
        //1.计算offset
        int offset = (page - 1) * pageSize;
        //2.调用mapper，查询分页数据
        return itemMapper.selectPage(offset, pageSize, keyword);
    }

    //查询商品总条数
    public Long countItemTotal(String keyword) {
        return itemMapper.countItemTotal(keyword);
    }
}
