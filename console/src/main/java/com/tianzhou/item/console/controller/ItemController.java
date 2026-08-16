package com.tianzhou.item.console.controller;

import com.tianzhou.item.console.domain.ItemListFeedVO;
import com.tianzhou.item.console.domain.ItemListVO;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public String createItem(
            @RequestParam(value = "coverImages") String coverImages,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "price") Float price,
            @RequestParam(value = "introduction") String introduction
    ) {
        log.info("新增商品，coverImages:{}，name:{}，price:{}，introduction:{}", coverImages, name, price, introduction);
        Long id = itemService.createItem(coverImages, name, price, introduction);
        return id != null ? "自增id是：" + id : "失败";
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
    public String updateItem(
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
    public String deleteItem(@RequestParam(value = "itemId") Long id) {
        log.info("根据商品id删除商品，itemId:{}", id);
        return itemService.deleteItem(id) > 0 ? "成功" : "失败";
    }

    /**
     * 查询商品列表
     *
     * @param page
     * @return
     */
    @RequestMapping("/item/list")
    public ItemListFeedVO list(@RequestParam(value = "page") Integer page) {
        //1.先定死pageSize=10
        int pageSize = 10;
        //2.查询分页数据
        List<Item> itemList = itemService.selectItemPage(page, pageSize);
        //3.查询总条数
        Long total = itemService.countItemTotal();
        //4.封装ItemListVO
        List<ItemListVO> itemListVOList = new ArrayList<>(itemList.size());
        for (Item item : itemList) {
            //4.1 按照$分割，拿到wallImage
            String wallImage = item.getCoverImages().split("\\$")[0];
            //4.2 往vo里设置属性
            ItemListVO itemListVO = new ItemListVO().setItemId(item.getId())
                    .setWallImage(wallImage)
                    .setName(item.getName())
                    .setPrice(item.getPrice().floatValue());
            //4.3 放进集合
            itemListVOList.add(itemListVO);
        }
        //5.返回
        return new ItemListFeedVO().setList(itemListVOList)
                .setTotal(total)
                .setPageSize(pageSize);
    }
}
