package com.tianzhou.item.app.controller;

import com.tianzhou.item.app.domain.ItemInfoVO;
import com.tianzhou.item.app.domain.ItemListFeedVO;
import com.tianzhou.item.app.domain.ItemListVO;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class AppController {

    @Autowired
    private AppService appService;

    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping("/item/list")
    public ItemListFeedVO list() {
        log.info("===查询商品列表===");

        //1.拿到商品列表
        List<Item> itemList = appService.list();
        //如果列表为空，直接返回
        if (itemList.isEmpty()) {
            return new ItemListFeedVO(new ArrayList<>());
        }
        //不为空，往下走
        List<ItemListVO> itemListVOList = new ArrayList<>(itemList.size());
        //2.循环拿东西
        for (Item item : itemList) {
            //3.拿到轮播图的第一张图，也就是wallImage
            String coverImages = item.getCoverImages();
            String wallImage = coverImages.split("\\$")[0];
            //4.为ItemListVO赋值
            ItemListVO itemListVO = new ItemListVO().setItemId(item.getId())
                    .setName(item.getName())
                    .setPrice(item.getPrice())
                    .setWallImage(wallImage);
            //5.添加进list中
            itemListVOList.add(itemListVO);
        }
        //6.返回
        return new ItemListFeedVO(itemListVOList);
    }

    /**
     * 根据商品id查询商品详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/item/info")
    public ItemInfoVO getItemInfo(@RequestParam(value = "itemId") BigInteger id) {
        log.info("根据商品id查询商品详情，itemId:{}", id);

        //1.拿到item对象
        Item item = appService.getItemInfo(id);
        //商品不存在，抛异常
        if (item == null) {
            throw new RuntimeException("商品不存在！");
        }
        //商品存在，往下走
        //2.按照$分割cover_images
        String coverImages = item.getCoverImages();
        String[] split = coverImages.split("\\$");
        //3.封装ItemInfoVO属性并返回
        return new ItemInfoVO().setCoverImages(split)
                .setName(item.getName())
                .setPrice(item.getPrice())
                .setIntroduction(item.getIntroduction());
    }
}
