package com.tianzhou.item.module.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.entity.ItemExportAndImport;
import com.tianzhou.item.module.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ItemImportListener extends AnalysisEventListener<ItemExportAndImport> {
    private static final int BATCH_SIZE = 20;
    private final List<Item> cache = new ArrayList<>(BATCH_SIZE);
    private final ItemService itemService;

    public ItemImportListener(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public void invoke(ItemExportAndImport itemExportAndImport, AnalysisContext analysisContext) {
        Item item = new Item();
        item.setCoverImages(itemExportAndImport.getCoverImages());
        item.setName(itemExportAndImport.getName());
        item.setPrice(itemExportAndImport.getPrice());
        item.setIntroduction(itemExportAndImport.getIntroduction());
        item.setCreateTime(itemExportAndImport.getCreateTime());
        item.setUpdateTime(itemExportAndImport.getUpdateTime());
        item.setIsDeleted(itemExportAndImport.getIsDeleted());
        item.setCategoryId(itemExportAndImport.getCategoryId());

        cache.add(item);
        if (cache.size() >= BATCH_SIZE) {
            itemService.insertBatch(new ArrayList<>(cache));
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
