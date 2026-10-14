package com.tianzhou.item.console.item.controller;

import cn.hutool.core.io.FileUtil;
import com.tianzhou.item.console.item.domain.ItemDTO;
import com.tianzhou.item.console.item.domain.ItemInfoVO;
import com.tianzhou.item.console.item.domain.ItemListFeedVO;
import com.tianzhou.item.console.item.domain.ItemListVO;
import com.tianzhou.item.module.category.entity.Category;
import com.tianzhou.item.module.category.service.CategoryService;
import com.tianzhou.item.module.common.Response;
import com.tianzhou.item.module.enums.ResponseCodeEnum;
import com.tianzhou.item.module.exception.BizException;
import com.tianzhou.item.module.item.entity.Item;
import com.tianzhou.item.module.item.entity.ItemWithCategory;
import com.tianzhou.item.module.item.service.ItemService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    public Response<String> createItem(
            @RequestParam(value = "coverImages") String coverImages,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "price") Float price,
            @RequestParam(value = "introduction") String introduction,
            @RequestParam(value = "categoryId") Long categoryId
    ) {
        log.info("新增商品，coverImages:{}，name:{}，price:{}，introduction:{},categoryId:{}", coverImages, name, price, introduction, categoryId);
        try {
            Long itemId = itemService.edit(null, coverImages, name.trim(), price, introduction, categoryId);
            return Response.success("自增id是：" + itemId);
        } catch (BizException e) {
            ResponseCodeEnum responseCodeEnum = e.getResponseCodeEnum();
            log.error("an error occurred", e);
            return Response.fail(responseCodeEnum);
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
    public Response<String> updateItem(
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
            return Response.success("修改商品的id是：" + itemId);
        } catch (BizException e) {
            ResponseCodeEnum responseCodeEnum = e.getResponseCodeEnum();
            log.error("an error occurred", e);
            return Response.fail(responseCodeEnum);
        }
    }

    /**
     * 根据商品id删除商品
     *
     * @param id
     * @return
     */
    @RequestMapping("/item/delete")
    public Response<String> deleteItem(@RequestParam(value = "itemId") Long id) {
        log.info("根据商品id删除商品，itemId:{}", id);
        int rows = itemService.delete(id);
        if (rows == 0) {
            return Response.fail(ResponseCodeEnum.ITEM_ALREADY_DELETED);
        }
        return Response.success();
    }

    /**
     * 查询商品分页列表，模糊查询
     *
     * @param page
     * @return
     */
    @RequestMapping("/item/list")
    public Response<ItemListFeedVO> list(@RequestParam(value = "page") Integer page,
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
        ItemListFeedVO itemListFeedVO = new ItemListFeedVO().setList(itemListVOList)
                .setTotal(total)
                .setPageSize(pageSize);
        return Response.success(itemListFeedVO);
    }

    /**
     * 根据商品id查询商品详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/item/info")
    public Response<ItemInfoVO> getItemInfo(@RequestParam(value = "itemId") Long id) {
        //1.调用service，拿到item对象
        Item item = itemService.getById(id);
        //如果item为null
        if (item == null) {
            return Response.fail(ResponseCodeEnum.ITEM_NOT_EXISTS);
        }

        //不为空
        //2.根据商品分类id查询分类信息
        Category category = categoryService.getById(item.getCategoryId());
        if (category == null) {
            return Response.fail(ResponseCodeEnum.CATEGORY_NOT_EXISTS);
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
        ItemInfoVO itemInfoVO = new ItemInfoVO().setCoverImages(Arrays.asList(coverImages))
                .setName(item.getName())
                //将BigDecimal转换成Float
                .setPrice(item.getPrice().floatValue())
                .setIntroduction(item.getIntroduction())
                .setCreateTime(createTime)
                .setUpdateTime(updateTime)
                .setCategoryName(category.getName())
                .setCategoryImage(category.getImage());
        return Response.success(itemInfoVO);
    }

    /**
     * 做个接收DTO测试，简单demo
     *
     * @param itemDTO
     * @return
     */
    @PostMapping("/item/insert")
    public Response<String> insertItem(@RequestBody ItemDTO itemDTO) {
        log.info("新增商品，itemDTO:{}", itemDTO);
        int i = 1 / 0;
        return Response.success("接收DTO成功");
    }

    @RequestMapping("/item/export")
    public void exportItemZip(HttpServletResponse response) throws IOException {
        // 生成zip文件
        File zipFile = null;
        try {
            zipFile = itemService.exportZip();
        } catch (InterruptedException e) {
            log.error("an error occurred", e);
        }

        // 设置响应头
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("商品导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition",
                "attachment;filename*=utf-8''" + fileName + ".zip");
        response.setContentLengthLong(zipFile.length());

        // 写zip到响应流
        try (InputStream in = new FileInputStream(zipFile);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }

        // 删除zip文件
        FileUtil.del(zipFile);
    }

    @PostMapping("/item/import")
    public Response<String> importItemZip(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Response.fail(ResponseCodeEnum.FILE_REQUIRED);
        }

        if (!file.getOriginalFilename().endsWith(".zip")) {
            return Response.fail(ResponseCodeEnum.FILE_NOT_ZIP);
        }

        try (InputStream inputStream = file.getInputStream()) {
            String result = itemService.importZip(inputStream);
            return Response.success(result);
        } catch (Exception e) {
            log.error("an error occurred", e);
            return Response.fail(ResponseCodeEnum.EXCEL_IMPORT_FAILED);
        }
    }
}
