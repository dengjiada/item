package com.tianzhou.item.app.controller;

import com.tianzhou.item.app.domain.ItemInfoVO;
import com.tianzhou.item.app.domain.ItemListFeedVO;
import com.tianzhou.item.app.domain.ItemListVO;
import com.tianzhou.item.module.entity.Category;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.service.CategoryService;
import com.tianzhou.item.module.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询商品分页列表，模糊查询
     *
     * @return
     */
    @RequestMapping("/item/list")
    public ItemListFeedVO list(@RequestParam(value = "page") Integer page,
                               @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("===查询商品列表===");
        //对keyword进行trim
        if (keyword != null) {
            keyword = keyword.trim();
        }

        //1.先定死pageSize是10
        int pageSize = 10;

        //2.拿到商品列表
        List<Item> itemList = itemService.selectItemPage(page, pageSize, keyword);

        //3.判断是否到瀑布流结尾，没有下一页
        //我的做法是拿到分页后的itemList的size，如果size小于pageSize（每页有多少条数据），说明是最后一页，返回false
        //如果相等，则认为不是最后一页，但实际上最后一页的数据条数也可能刚好和pageSize相等，如果是这样的话，再多查一次就行
        boolean isEnd = itemList.size() < pageSize;

        //4.封装VO
        List<ItemListVO> itemListVOList = new ArrayList<>(itemList.size());
        for (Item item : itemList) {
            //4.1根据商品分类id查询分类信息
            Category category = categoryService.getById(item.getCategoryId());
            if (category == null) {
                //没有该分类的话不展示该商品
                continue;
            }
            //4.2拿到轮播图的第一张图，也就是wallImage
            String wallImage = item.getCoverImages().split("\\$")[0];
            //4.3为ItemListVO赋值
            ItemListVO itemListVO = new ItemListVO().setItemId(item.getId())
                    .setName(item.getName())
                    //BigDecimal转换成Float
                    .setPrice(item.getPrice().floatValue())
                    .setWallImage(wallImage)
                    .setCategoryName(category.getName());
            //4.4添加进list中
            itemListVOList.add(itemListVO);
        }
        //5.返回
        return new ItemListFeedVO().setList(itemListVOList)
                .setIsEnd(isEnd);
    }

    /**
     * 根据商品id查询商品详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/item/info")
    public ItemInfoVO getItemInfo(@RequestParam(value = "itemId") Long id) {
        log.info("根据商品id查询商品详情，itemId:{}", id);

        //1.拿到item对象
        Item item = itemService.getById(id);
        //商品不存在
        if (item == null) {
            //返回一个VO空对象，这个阶段先这样搞，后续可能修改
            return new ItemInfoVO();
        }

        //商品存在，往下走
        //2.根据商品分类id查询分类信息
        Category category = categoryService.getById(item.getCategoryId());
        if (category == null) {
            //todo 先返回null，后续会改掉
            return new ItemInfoVO();
        }

        //3.按照$分割cover_images
        String coverImages = item.getCoverImages();
        String[] split = coverImages.split("\\$");

        //4.封装ItemInfoVO属性并返回
        return new ItemInfoVO().setCoverImages(Arrays.asList(split))
                .setName(item.getName())
                //BigDecimal转换成Float
                .setPrice(item.getPrice().floatValue())
                .setIntroduction(item.getIntroduction())
                .setCategoryName(category.getName())
                .setCategoryImage(category.getImage());
    }

    /**
     * 测试全局异常处理类
     *
     * @return
     */
    @RequestMapping("/item/test")
    public String testGlobalExceptionHandler() {
        int i = 1 / 0;
        return "OK";
    }
}
