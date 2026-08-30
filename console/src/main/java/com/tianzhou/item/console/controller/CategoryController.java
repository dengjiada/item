package com.tianzhou.item.console.controller;

import com.tianzhou.item.console.domain.CategoryInfoVO;
import com.tianzhou.item.console.domain.CategoryListFeedVO;
import com.tianzhou.item.console.domain.CategoryListVO;
import com.tianzhou.item.module.entity.Category;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.service.CategoryService;
import com.tianzhou.item.module.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author test
 * @since 2026-08-29
 */
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ItemService itemService;

    /**
     * 查询分类列表
     *
     * @return
     */
    @RequestMapping("/category/list")
    public CategoryListFeedVO list() {
        //1.查询category列表
        List<Category> categoryList = categoryService.list();
        //2.封装VO
        List<CategoryListVO> categoryListVOList = new ArrayList<>(categoryList.size());
        for (Category category : categoryList) {
            CategoryListVO categoryListVO = new CategoryListVO()
                    .setCategoryId(category.getId())
                    .setName(category.getName())
                    .setImage(category.getImage());
            categoryListVOList.add(categoryListVO);
        }
        //3.返回
        return new CategoryListFeedVO().setList(categoryListVOList);
    }

    /**
     * 根据分类id查询分类详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/category/info")
    public CategoryInfoVO getInfo(@RequestParam(value = "categoryId") Long id) {
        //1.根据id查询category
        Category category = categoryService.getById(id);
        if (category == null) {
            return new CategoryInfoVO();
        }
        //2.解析category的创建时间和更新时间
        //2.1拿到创建时间和更新时间的秒级时间戳
        Integer categoryCreateTime = category.getCreateTime();
        Integer categoryUpdateTime = category.getUpdateTime();
        //2.2指定要格式化的时间格式
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //2.3指定时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        //2.4将时间戳转换成Local DATe Time对象
        LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(categoryCreateTime.longValue()), zoneId);
        LocalDateTime updateLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(categoryUpdateTime.longValue()), zoneId);
        //2.5转换成指定格式
        String createTime = createLocalDateTime.format(pattern);
        String updateTime = updateLocalDateTime.format(pattern);
        //3.封装VO并返回
        return new CategoryInfoVO().setName(category.getName())
                .setImage(category.getImage())
                .setCreateTime(createTime)
                .setUpdateTime(updateTime);
    }

    /**
     * 新增分类
     *
     * @param name
     * @param image
     * @return
     */
    @RequestMapping("/category/create")
    public String create(@RequestParam(value = "name") String name,
                         @RequestParam(value = "image") String image) {
        int rows = categoryService.insert(name, image);
        return rows > 0 ? "成功" : "失败";
    }

    /**
     * 根据分类id修改分类
     *
     * @param id
     * @param name
     * @param image
     * @return
     */
    @RequestMapping("/category/update")
    public String update(@RequestParam(value = "categoryId") Long id,
                         @RequestParam(value = "name") String name,
                         @RequestParam(value = "image") String image) {
        int rows = categoryService.update(id, name, image);
        return rows > 0 ? "成功" : "失败";
    }

    /**
     * 根据分类id逻辑删除分类
     *
     * @param id
     * @return
     */
    @RequestMapping("/category/delete")
    public String delete(@RequestParam(value = "categoryId") Long id) {
        //1.先根据分类id查询该分类下的商品
        List<Item> itemList = itemService.getItemByCategoryId(id);
        //2.如果有商品，那么该分类则不能删除
        if (!CollectionUtils.isEmpty(itemList)){
            return "当前分类下有商品，不能删除";
        }
        //3.如果没有商品，正常删除
        int rows = categoryService.delete(id);
        return rows > 0 ? "成功" : "失败";
    }
}