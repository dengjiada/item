package com.tianzhou.item.module.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.tianzhou.item.module.entity.Category;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.entity.ItemExportAndImport;
import com.tianzhou.item.module.entity.ItemWithCategory;
import com.tianzhou.item.module.listener.ItemImportListener;
import com.tianzhou.item.module.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@Service
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    @Qualifier("itemExportAndImportExecutor")
    private ThreadPoolTaskExecutor executor;

    //根据分类id查询商品
    public List<Item> getItemByCategoryId(Long categoryId) {
        return itemMapper.getItemByCategoryId(categoryId);
    }

    //根据商品id查询商品详情，需要判断逻辑删除标记（is_deleted）
    public Item getById(Long id) {
        return itemMapper.getById(id);
    }

    //根据商品id查询商品详情，不需要判断逻辑删除标记（is_deleted）
    public Item extractById(Long id) {
        return itemMapper.extractById(id);
    }

    //新增商品
    public int insert(Item item) {
        return itemMapper.insert(item);
    }

    //修改商品
    public int update(Item item) {
        return itemMapper.update(item);
    }

    //将insert，update合成成edit
    public Long edit(Long id, String coverImages, String name, Float price, String introduction, Long categoryId) {
        //1. 校验参数
        //1.1 校验coverImages
        if (coverImages == null || coverImages.isEmpty()) {
            throw new RuntimeException("coverImages cannot be empty");
        }
        //1.2 校验name
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("name cannot be empty");
        }
        if (name.length() > 50) {
            throw new RuntimeException("name length cannot exceed 50");
        }
        //1.3 校验price
        if (price == null) {
            throw new RuntimeException("price cannot be null");
        }
        if (price < 0) {
            throw new RuntimeException("price cannot be negative");
        }
        //1.4 校验introduction
        if (introduction == null) {
            throw new RuntimeException("introduction cannot be null");
        }
        if (introduction.length() > 2000) {
            throw new RuntimeException("introduction length cannot exceed 2000");
        }
        //1.5 校验categoryId
        //根据商品分类id查询分类信息
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            throw new RuntimeException("category id not exist");
        }

        //2.创建item对象
        Item item = new Item().setCoverImages(coverImages)
                .setName(name)
                .setPrice(BigDecimal.valueOf(price))
                .setIntroduction(introduction)
                .setCategoryId(categoryId);

        //3. 根据id是否为null来决定进入的是insert分支还是update分支
        //3.1 id为null，进入insert分支
        if (id == null) {
            //3.1.1 为insert的对象添加剩余的值
            item.setCreateTime((int) (System.currentTimeMillis() / 1000L)).setIsDeleted(0);
            //3.1.2 调用insert方法
            int rows = insert(item);
            //3.1.3 如果rows=0，认为insert失败，抛异常
            if (rows == 0) {
                throw new RuntimeException("create item failed");
            }
            //3.1.4 insert成功，返回insert生成的id
            return item.getId();
        }
        //3.2 id不为null，进入update分支
        //3.2.1 校验id
        //查询id对应的entity是否在数据库中
        Item item1 = extractById(id);
        if (item1 == null) {
            throw new RuntimeException("item id not exist");
        }
        //3.2.2 为update的对象添加剩余的值
        item.setId(id);
        //3.2.3 调用update方法
        int rows = update(item);
        //3.2.4 如果rows=0，认为update失败，抛异常
        if (rows == 0) {
            throw new RuntimeException("update item failed");
        }
        //3.2.5 update成功，返回Item的id
        return id;
    }

    //根据商品id删除商品
    public int delete(Long id) {
        return itemMapper.delete(id, (int) (System.currentTimeMillis() / 1000L));
    }

    //查询商品列表分页数据
    public List<Item> selectItemPage(int page, int pageSize, String keyword) {
        //1.计算offset
        int offset = (page - 1) * pageSize;
        String ids = null;
        //关键词不为空
        if (keyword != null && !keyword.isEmpty()) {
            //2.1 子sql，查询符合的商品分类ids
            List<Long> idList = categoryService.selectCategoryIdsByCategoryName(keyword);
            //2.2 拼接ids
            if (idList.isEmpty()) {
                ids = "0";
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idList.size(); i++) {
                    sb.append(idList.get(i));
                    if (i != idList.size() - 1) {
                        sb.append(",");
                    }
                }
                ids = sb.toString();
            }
        }

        //3.调用mapper，查询分页数据
        //如果keyword为空的话，就意味着要查询所有商品，就没必要再查询分类表了，即ids为null
        return itemMapper.selectItemPage(offset, pageSize, keyword, ids);
    }

    //联表查询商品列表分页数据
    public List<ItemWithCategory> selectItemWithCategoryPage(int page, int pageSize, String keyword) {
        //1.计算offset
        int offset = (page - 1) * pageSize;

        //2.调用mapper，查询分页数据
        return itemMapper.selectItemWithCategoryPage(offset, pageSize, keyword);
    }

    //查询商品总条数
    public Long countItemTotal(String keyword) {
        return itemMapper.countItemTotal(keyword);
    }

    //批量新增商品
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(List<Item> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        itemMapper.insertBatch(list);
    }

    //导出商品表，并压缩成zip
    public File exportZip() throws InterruptedException {
        // 在当前的操作系统的临时目录创建一个存放excel文件的临时目录
        File tempDir = FileUtil.file(System.getProperty("java.io.tmpdir"),
                "item_export_" + System.currentTimeMillis());
        FileUtil.mkdir(tempDir);

        // 创建latch
        CountDownLatch latch = new CountDownLatch(10);

        // 开启10个线程，创建10个任务，每个任务负责一部分
        for (int mod = 0; mod < 10; mod++) {
            final int currentMod = mod;
            executor.submit(() -> {
                try {
                    // 要生成的excel文件的路径
                    String filePath = tempDir.getAbsolutePath()
                            + File.separator + "商品_" + currentMod + ".xlsx";

                    // 将商品表的该分片的商品写入excel文件中
                    writeExcel(filePath, currentMod);
                } catch (Exception e) {
                    throw new RuntimeException("导出分片 " + currentMod + " 失败", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        //等待10个线程完工
        latch.await();

        // 压缩成zip
        File zipFile = FileUtil.file(tempDir.getParent(), "商品导出_" + System.currentTimeMillis() + ".zip");
        ZipUtil.zip(zipFile, true, tempDir);

        //删除临时目录
        FileUtil.del(tempDir);

        //返回zip文件
        return zipFile;
    }

    //将商品表中的数据写到excel文件
    private void writeExcel(String filePath, int currentMod) {
        //分页查询，一次查询1000条
        int pageNo = 1;
        int pageSize = 1000;

        //创建流
        try (ExcelWriter writer = EasyExcel.write(filePath, ItemExportAndImport.class).build()) {
            WriteSheet sheet = EasyExcel.writerSheet("商品信息").build();

            while (true) {
                int offset = (pageNo - 1) * pageSize;
                List<ItemExportAndImport> list = itemMapper.selectItemPageByMod(offset, pageSize, currentMod);
                if (list == null || list.isEmpty()) {
                    break;
                }

                //导出
                writer.write(list, sheet);

                if (list.size() < pageSize) {
                    break;
                }
                pageNo++;
            }
        }
    }

    //解压zip，并读取其中excel而后导入到商品表
    public String importZip(InputStream inputStream) throws InterruptedException {
        //创建一个临时目录，用于zip文件解压后的存放的位置
        File tempDir = FileUtil.file(System.getProperty("java.io.tmpdir"),
                "item_import_" + System.currentTimeMillis());
        FileUtil.mkdir(tempDir);

        try {
            //解压zip到临时目录
            ZipUtil.unzip(inputStream, tempDir, Charset.forName("GBK"));

            //收集临时目录中的excel文件
            //递归遍历临时目录中文件，拿到excel文件
            List<File> excelFiles = FileUtil.loopFiles(tempDir,
                    file -> file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls"));
            if (excelFiles == null || excelFiles.isEmpty()) {
                return "未找到excel文件";
            }

            //做好多线程读取临时目录中的excel文件的准备
            CountDownLatch latch = new CountDownLatch(excelFiles.size());
            //记录错误信息
            List<String> errors = new CopyOnWriteArrayList<>();

            //多线程读取excel文件
            for (File excelFile : excelFiles) {
                executor.submit(() -> {
                    try {
                        EasyExcel.read(excelFile,
                                        ItemExportAndImport.class,
                                        new ItemImportListener(this))
                                .sheet()
                                .doRead();
                    } catch (Exception e) {
                        errors.add(excelFile.getName() + ":" + e.getMessage());
                    } finally {
                        //计数器减一
                        latch.countDown();
                    }
                });
            }

            //等待所有线程读取完毕
            latch.await();

            if (!errors.isEmpty()) {
                return "excel文件导入失败:" + errors;
            }
            return "excel文件导入成功";
        } finally {
            //读取完后将临时目录删除
            FileUtil.del(tempDir);
        }
    }
}
