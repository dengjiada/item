package com.tianzhou.item.module.service;

import com.tianzhou.item.module.entity.Category;
import com.tianzhou.item.module.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类表 业务层
 *
 * @author test
 * @since 2026-08-29
 */

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    //根据id查询分类详情，需要判断逻辑删除标记（is_deleted）
    public Category getById(Long id) {
        return categoryMapper.getById(id);
    }

    //根据id查询分类详情，不需要判断逻辑删除标记（is_deleted）
    public Category extractById(Long id) {
        return categoryMapper.extractById(id);
    }

    //新增分类
    public int insert(String name, String image) {
        Category entity = new Category().setName(name)
                .setImage(image)
                .setCreateTime((int) (System.currentTimeMillis() / 1000L));
        return categoryMapper.insert(entity);
    }

    //修改分类
    public int update(Long id, String name, String image) {
        Category category = new Category().setId(id)
                .setName(name)
                .setImage(image);
        return categoryMapper.update(category);
    }

    //根据id删除逻辑删除分类
    public int delete(Long id) {
        return categoryMapper.delete(id, (int) (System.currentTimeMillis() / 1000L));
    }

    //查询分类列表
    public List<Category> list() {
        return categoryMapper.list();
    }

    //根据关键词查询符合的商品id
    public List<Long> selectCategoryIdsByCategoryName(String keyword) {
        return categoryMapper.selectCategoryIdsByCategoryName(keyword);
    }
}
