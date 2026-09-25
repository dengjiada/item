package com.tianzhou.item.console.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tianzhou.item.console.domain.ItemExportAndImportVO;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.service.ItemService;

import java.util.ArrayList;
import java.util.List;

public class ItemImportListener extends AnalysisEventListener<ItemExportAndImportVO> {
    private static final int BATCH_SIZE = 1000;
    private final List<Item> cache = new ArrayList<>(BATCH_SIZE);
    private final ItemService itemService;

    public ItemImportListener(ItemService itemService) {
        this.itemService = itemService;
    }


    @Override
    public void invoke(ItemExportAndImportVO itemExportAndImportVO, AnalysisContext analysisContext) {
        Item item = new Item();
        item.setCoverImages(itemExportAndImportVO.getCoverImages());
        item.setName(itemExportAndImportVO.getName());
        item.setPrice(itemExportAndImportVO.getPrice());
        item.setIntroduction(itemExportAndImportVO.getIntroduction());
        item.setCreateTime(itemExportAndImportVO.getCreateTime());
        item.setUpdateTime(itemExportAndImportVO.getUpdateTime());
        item.setIsDeleted(itemExportAndImportVO.getIsDeleted());
        item.setCategoryId(itemExportAndImportVO.getCategoryId());

        cache.add(item);
        if (cache.size() >= BATCH_SIZE) {
            itemService.insertBatch(cache);
            cache.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!cache.isEmpty()) {
            itemService.insertBatch(cache);
            cache.clear();
        }
    }
}
