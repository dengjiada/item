package com.tianzhou.item.module.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.tianzhou.item.module.entity.File;
import com.tianzhou.item.module.enums.FileTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class UploadService {
    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.domain}")
    private String domain;

    @Autowired
    private FileService fileService;

    //上传图片/视频/文件
    public String upload(String type, String originalFilename, byte[] fileBytes, String contentType) throws IOException {
        // 根据文件类型获得对应的枚举
        FileTypeEnum fileType = FileTypeEnum.fromOssDir(type);

        // 获得yyMM/dd
        LocalDate now = LocalDate.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyMM/dd");
        String format = now.format(pattern);

        // 随机random(1~1000)的数
        Random random = new Random();
        int randomNum = random.nextInt(1, 1001);

        // 毫秒级时间戳
        long currentTimeMillis = System.currentTimeMillis();

        String nonce = String.valueOf(randomNum) + currentTimeMillis;
        // 使用MD5加密
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(nonce.getBytes(StandardCharsets.UTF_8));

        // 获得原尾缀
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

        // 去掉开头的斜杠
        String cleanDir = fileType.getOssDir().startsWith("/") ? fileType.getOssDir().substring(1) : fileType.getOssDir();
        String newFileName;//新文件名

        // 判断文件是image/video/file中的哪一种
        if (fileType == FileTypeEnum.IMAGE) {
            //image
            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new RuntimeException("Cannot parse image format. Please upload a valid image file");
                }
                // 获得宽和高
                int width = image.getWidth();
                int height = image.getHeight();
                // 拼接新文件名
                newFileName = cleanDir + "/" + format + "/" + md5DigestAsHex + "_" + width + "x" + height + "." + extension;
            }
        } else {
            // 如果是video或者是file
            // 拼接新文件名
            newFileName = cleanDir + "/" + format + "/" + md5DigestAsHex + "." + extension;

        }

        uploadFile(newFileName, fileBytes, contentType);

        //url
        String url = domain + "/" + newFileName;
        //写入数据库
        File file = new File().setType(fileType.getCode())
                .setUrl(url)
                .setOriginalName(originalFilename)
                .setObjectName(newFileName)
                .setCreateTime((int) (System.currentTimeMillis() / 1000L));
        fileService.insert(file);

        //返回的url
        return url;
    }

    private void uploadFile(String newFileName, byte[] fileBytes, String contentType) {
        try {
            // 创建上传文件的元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileBytes.length);
            metadata.setContentType(contentType);

            // 将 byte[] 转换为 ByteArrayInputStream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

            // 创建 PutObjectRequest，传入的是输入流和元数据
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, newFileName, inputStream, metadata);
            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
    }
}
