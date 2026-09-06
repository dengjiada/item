package com.tianzhou.item.module.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class UploadService {
    //上传图片/视频/文件
    public String upload(String originalFilename, String contentType, byte[] fileBytes, String rootPath) throws IOException {
        //1. 随机random(1~1000)的数
        Random random = new Random();
        int randomNum = random.nextInt(1, 1001);

        //2. 毫秒级时间戳
        long currentTimeMillis = System.currentTimeMillis();

        //3. 获得原尾缀
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("Filename cannot be empty");
        }
        int index = originalFilename.lastIndexOf(".");//最后一个"."的索引位置
        String extension;
        if (index > 0) {//获取尾缀
            extension = originalFilename.substring(index + 1).toLowerCase();
        } else {//获取不到，给个默认尾缀
            extension = "file";
        }

        String newFileName;//新文件名
        String fileType;//文件类型

        //4. 判断文件是image/video/file中的哪一种
        if (contentType != null && contentType.startsWith("image/")) {
            fileType = "image";
            //image
            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new RuntimeException("Cannot parse image format. Please upload a valid image file");
                }
                //5. 获得宽和高
                int width = image.getWidth();
                int height = image.getHeight();
                //6. 拼接新文件名
                newFileName = String.valueOf(randomNum) + currentTimeMillis + "_" + width + "x" + height + "." + extension;
            }
        } else if (contentType != null && contentType.startsWith("video/")) {
            //video
            fileType = "video";

            //5. 拼接新文件名
            newFileName = String.valueOf(randomNum) + currentTimeMillis + "." + extension;
        } else {
            //其余分流到file
            fileType = "file";

            //5. 拼接新文件名
            newFileName = String.valueOf(randomNum) + currentTimeMillis + "." + extension;
        }
        //6. 获取完整路径
        Path fullPath = Paths.get(rootPath, "upload", fileType, newFileName);

        //7. 创建父级路径
        Files.createDirectories(fullPath.getParent());

        //8. 将文件存入对应的路径
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            Files.copy(inputStream, fullPath);
        }

        return "/upload/" + fileType + "/" + newFileName;
    }
}
