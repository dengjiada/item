package com.tianzhou.item.module.service;

import com.tianzhou.item.module.entity.File;
import com.tianzhou.item.module.mapper.FileMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 文件类型表 业务层
 * @author test
 * @since 2026-09-19
 */

@Service
public class FileService {
    @Autowired
    private FileMapper mapper;

    //根据id查询详情，需要判断逻辑删除标记（is_deleted）
    public File getById(Long id) {
        return mapper.getById(id);
    }

    //根据id查询详情，不需要判断逻辑删除标记（is_deleted）
    public File extractById(Long id) {
        return mapper.extractById(id);
    }

    //新增
    public int insert(File entity) {
        return mapper.insert(entity);
    }

    //修改
    public int update(File entity) {
        return mapper.update(entity);
    }

    //根据id删除逻辑删除
    public int delete(Long id) {
        return mapper.delete(id,(int) (System.currentTimeMillis() / 1000L));
    }
}
