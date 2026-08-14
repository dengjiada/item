package com.tianzhou.item.console.controller;

import com.tianzhou.item.module.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 新增商品
     *
     * @param coverImages
     * @param name
     * @param price
     * @param introduction
     * @return
     */
    @RequestMapping("/item/create")
    private String createItem(
            @RequestParam(value = "coverImages") String coverImages,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "price") Float price,
            @RequestParam(value = "introduction") String introduction
    ) {
        log.info("新增商品，coverImages:{}，name:{}，price:{}，introduction:{}", coverImages, name, price, introduction);
        return itemService.createItem(coverImages, name, price, introduction) > 0 ? "成功" : "失败";
    }

    /**
     * 根据商品id修改商品信息
     *
     * @param id
     * @param coverImages
     * @param name
     * @param price
     * @param introduction
     * @return
     */
    @RequestMapping("/item/update")
    private String updateItem(
            @RequestParam(value = "itemId") Long id,
            @RequestParam(value = "coverImages") String coverImages,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "price") Float price,
            @RequestParam(value = "introduction") String introduction
    ) {
        log.info("根据商品id修改商品，itemId:{}，coverImages:{}，name:{}，price:{}，introduction:{}", id, coverImages, name, price, introduction);
        return itemService.updateItem(id, coverImages, name, price, introduction) > 0 ? "成功" : "失败";
    }

    /**
     * 根据商品id删除商品
     *
     * @param id
     * @return
     */
    @RequestMapping("/item/delete")
    private String deleteItem(@RequestParam(value = "itemId") Long id) {
        log.info("根据商品id删除商品，itemId:{}", id);
        return itemService.deleteItem(id) > 0 ? "成功" : "失败";
    }
}
