package com.tianzhou.item.module.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ImageUtils {

    public static final Pattern AR_PATTERN = Pattern.compile("_(\\d+)x(\\d+)\\.[^.]+$");

    private ImageUtils() {
    }

    public static float getAr(OSS ossClient, String bucketName, String objectNameOrUrl) {
        // 预处理：如果是完整 URL，先提取真正的 objectName
        String objectName = extractObjectName(objectNameOrUrl);
        if (objectName.isEmpty()) {
            return 0f;
        }

        // 根据url来获得AR，利用正则匹配获取图⽚的“宽 和 ⾼”
        Matcher matcher = AR_PATTERN.matcher(objectName);
        if (matcher.find()) {
            // 能取到宽和高，并且高不为0
            float width = Float.parseFloat(matcher.group(1));
            float height = Float.parseFloat(matcher.group(2));
            if (height != 0) {
                return width / height;
            }
        }

        // 取不到宽和高 或者 高为0
        //根据url计算图片
        // 获取输入流
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            try (InputStream inputStream = ossObject.getObjectContent()) {
                // 使用 ImageIO 读取图片
                BufferedImage image = ImageIO.read(inputStream);
                if (image != null && image.getHeight() != 0) {
                    return (float) image.getWidth() / image.getHeight();
                }
            }
        } catch (Exception e) {
            log.error("an error occurred", e);
        }

        return 0f;
    }

    /**
     * 从完整 URL 中提取 OSS 的 objectName
     */
    private static String extractObjectName(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        // 如果是完整 URL，截取 .com/ 后面的部分
        if (url.startsWith("http://") || url.startsWith("https://")) {
            int index = url.indexOf(".com/");
            if (index != -1) {
                return url.substring(index + 5);
            }
        }
        return url;
    }
}
