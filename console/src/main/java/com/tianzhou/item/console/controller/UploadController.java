package com.tianzhou.item.console.controller;

import com.tianzhou.item.module.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class UploadController {
    @Autowired
    private UploadService uploadService;
    @Value("${file.upload.root-path}")
    private String rootPath;

    /**
     * 上传图片/视频/文件
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public String upload(@RequestParam(value = "file") MultipartFile file) {
        if (file.isEmpty()) {
            return "请选择一个有效的文件";
        }
        try {
            return uploadService.upload(file.getOriginalFilename(), file.getContentType(), file.getBytes(), rootPath);
        } catch (Exception e) {
            log.error("an error occurred", e);
            return "上传失败";
        }
    }
}
