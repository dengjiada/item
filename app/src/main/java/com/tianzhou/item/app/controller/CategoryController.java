package com.tianzhou.item.app.controller;

import com.tianzhou.item.app.domain.CategoryListFeedVO;
import com.tianzhou.item.app.domain.CategoryListVO;
import com.tianzhou.item.module.entity.Category;
import com.tianzhou.item.module.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
            CategoryListVO categoryListVO = new CategoryListVO().setCategoryId(category.getId())
                    .setName(category.getName())
                    .setImage(category.getImage());
            categoryListVOList.add(categoryListVO);
        }
        //3.返回
        return new CategoryListFeedVO().setList(categoryListVOList);
    }
}