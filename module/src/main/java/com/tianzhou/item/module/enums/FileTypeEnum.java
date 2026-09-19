package com.tianzhou.item.module.enums;

import lombok.Getter;

@Getter
public enum FileTypeEnum {
    IMAGE(1, "/image", "图片"),
    VIDEO(2, "/video", "视频"),
    FILE(3, "/file", "文件");

    private final int code;
    private final String ossDir;
    private final String desc;

    FileTypeEnum(int code, String ossDir, String desc) {
        this.code = code;
        this.ossDir = ossDir;
        this.desc = desc;
    }

    public static FileTypeEnum fromCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("File type code must not be null");
        }

        for (FileTypeEnum value : FileTypeEnum.values()) {
            if (code == value.code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown file type code: " + code);
    }

    public static FileTypeEnum fromOssDir(String ossDir) {
        if (ossDir == null || ossDir.isBlank()) {
            throw new IllegalArgumentException("OSS dir must not be blank");
        }

        String normalized = ossDir.startsWith("/") ? ossDir : "/" + ossDir;
        for (FileTypeEnum value : FileTypeEnum.values()) {
            if (value.ossDir.equals(normalized)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown OSS dir: " + ossDir);
    }
}
