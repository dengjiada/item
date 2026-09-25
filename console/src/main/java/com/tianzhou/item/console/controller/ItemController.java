package com.tianzhou.item.console.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.tianzhou.item.console.domain.*;
import com.tianzhou.item.console.listener.ItemImportListener;
import com.tianzhou.item.module.entity.Category;
import com.tianzhou.item.module.entity.Item;
import com.tianzhou.item.module.entity.ItemWithCategory;
import com.tianzhou.item.module.service.CategoryService;
import com.tianzhou.item.module.service.ItemService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
            @RequestParam(value = "introduction") String introduction,
            @RequestParam(value = "categoryId") Long categoryId
    ) {
        log.info("新增商品，coverImages:{}，name:{}，price:{}，introduction:{},categoryId:{}", coverImages, name, price, introduction, categoryId);
        try {
            Long itemId = itemService.edit(null, coverImages, name.trim(), price, introduction, categoryId);
            return "自增id是：" + itemId;
        } catch (RuntimeException e) {
            log.error("an error occurred", e);
            String msg = e.getMessage();
            if ("create item failed".equals(msg)) {
                return "新增失败";
            }
            // 参数校验类错误统一返回失败
            return "失败";
        }
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
            @RequestParam(value = "introduction") String introduction,
            @RequestParam(value = "categoryId") Long categoryId
    ) {
        log.info("根据商品id修改商品，itemId:{}，coverImages:{}，name:{}，price:{}，introduction:{},categoryId:{}", id, coverImages, name, price, introduction, categoryId);
        try {
            Long itemId = itemService.edit(id, coverImages, name.trim(), price, introduction, categoryId);
            return "修改商品的id是：" + itemId;
        } catch (RuntimeException e) {
            log.error("an error occurred", e);
            String msg = e.getMessage();
            if ("item id not exist".equals(msg)) {
                return "id不存在";
            }
            if ("update item failed".equals(msg)) {
                return "更新失败";
            }
            return "失败";
        }
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
        return itemService.delete(id) > 0 ? "成功" : "失败";
    }

    /**
     * 查询商品分页列表，模糊查询
     *
     * @param page
     * @return
     */
    @RequestMapping("/item/list")
    public ItemListFeedVO list(@RequestParam(value = "page") Integer page,
                               @RequestParam(value = "keyword", required = false) String keyword) {
        //对keyword进行trim
        if (keyword != null) {
            keyword = keyword.trim();
        }

        //1.先定死pageSize=10
        int pageSize = 10;

        //2.用联表查询分页数据
        List<ItemWithCategory> itemWithCategoryListList = itemService.selectItemWithCategoryPage(page, pageSize, keyword);

        //3.查询符合条件的总条数
        Long total = itemService.countItemTotal(keyword);

        //4.封装ItemListVO
        List<ItemListVO> itemListVOList = new ArrayList<>(itemWithCategoryListList.size());
        for (ItemWithCategory itemWithCategory : itemWithCategoryListList) {
            //4.1 根据商品分类id查询分类信息  改为  分类名为空即跳过展示
            if (itemWithCategory.getCategoryName() == null) {
                continue;
            }
            //4.2 按照$分割，拿到wallImage
            String wallImage = itemWithCategory.getCoverImages().split("\\$")[0];
            //4.3 往vo里设置属性
            ItemListVO itemListVO = new ItemListVO().setItemId(itemWithCategory.getItemId())
                    .setWallImage(wallImage)
                    .setName(itemWithCategory.getItemName())
                    .setPrice(itemWithCategory.getPrice().floatValue())
                    .setCategoryName(itemWithCategory.getCategoryName());
            //4.4 放进集合
            itemListVOList.add(itemListVO);
        }

        //5.返回
        return new ItemListFeedVO().setList(itemListVOList)
                .setTotal(total)
                .setPageSize(pageSize);
    }

    /**
     * 根据商品id查询商品详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/item/info")
    public ItemInfoVO getItemInfo(@RequestParam(value = "itemId") Long id) {
        //1.调用service，拿到item对象
        Item item = itemService.getById(id);
        //如果item为null
        if (item == null) {
            //返回一个VO空对象，这个阶段先这样搞，后续可能修改
            return new ItemInfoVO();
        }

        //不为空
        //2.根据商品分类id查询分类信息
        Category category = categoryService.getById(item.getCategoryId());
        if (category == null) {
            //todo 先返回null，后续会改掉
            return new ItemInfoVO();
        }

        //3.解析item的创建时间和更新时间
        //3.1拿到创建时间和更新时间的时间戳
        Integer itemCreateTime = item.getCreateTime();
        Integer itemUpdateTime = item.getUpdateTime();
        //3.2指定要转化的时间格式
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //3.3指定所在时区
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        //3.4将时间戳转换成LocalDateTime
        LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(itemCreateTime.longValue()), zone);
        LocalDateTime updateLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(itemUpdateTime.longValue()), zone);
        //3.5转换成对应时间格式
        String createTime = createLocalDateTime.format(pattern);
        String updateTime = updateLocalDateTime.format(pattern);

        //4.解析轮播图
        String[] coverImages = item.getCoverImages().split("\\$");

        //5.封装ItemInfoVO并返回
        return new ItemInfoVO().setCoverImages(Arrays.asList(coverImages))
                .setName(item.getName())
                //将BigDecimal转换成Float
                .setPrice(item.getPrice().floatValue())
                .setIntroduction(item.getIntroduction())
                .setCreateTime(createTime)
                .setUpdateTime(updateTime)
                .setCategoryName(category.getName())
                .setCategoryImage(category.getImage());
    }

    /**
     * 做个接收DTO测试，简单demo
     *
     * @param itemDTO
     * @return
     */
    @PostMapping("/item/insert")
    public String insertItem(@RequestBody ItemDTO itemDTO) {
        log.info("新增商品，itemDTO:{}", itemDTO);
        int i = 1 / 0;
        return "接收DTO成功!";
    }

    @RequestMapping("/item/export")
    public void exportItem(HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("商品信息表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition",
                "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 设置向浏览器输出数据的流
        ExcelWriter writer = EasyExcel.write(response.getOutputStream()).build();
        WriteSheet sheet = EasyExcel.writerSheet("商品信息").head(ItemExportAndImportVO.class).build();

        //分页查询，每次查询2000条
        int pageNo = 1;
        int pageSize = 2000;

        while (true) {
            List<Item> list = itemService.selectItemPage(pageNo, pageSize, null);
            if (list == null || list.isEmpty()) {
                break;
            }
            List<ItemExportAndImportVO> voList = new ArrayList<>(list.size());

            for (Item item : list) {
                ItemExportAndImportVO vo = new ItemExportAndImportVO();
                vo.setCoverImages(item.getCoverImages());
                vo.setName(item.getName());
                vo.setPrice(item.getPrice());
                vo.setIntroduction(item.getIntroduction());
                vo.setCreateTime(item.getCreateTime());
                vo.setUpdateTime(item.getUpdateTime());
                vo.setIsDeleted(item.getIsDeleted());
                vo.setCategoryId(item.getCategoryId());

                voList.add(vo);
            }

            writer.write(voList, sheet);
            if (list.size() < pageSize) {
                break;
            }
            pageNo++;
        }

        //必须finish，否则文件可能不完整
        writer.finish();
    }

    @PostMapping("/item/import")
    public String importItem(@RequestParam("file")MultipartFile file) throws IOException {
        if (file.isEmpty()){
            return "请选择一个有效的文件";
        }

        try {
            EasyExcel.read(file.getInputStream(),
                    ItemExportAndImportVO.class,
                    new ItemImportListener(itemService))
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            log.error("an error occurred", e);
            return "导入失败";
        }
        return "导入成功";
    }
}
