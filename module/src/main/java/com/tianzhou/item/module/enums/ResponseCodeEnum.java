package com.tianzhou.item.module.enums;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {
    SUCCESS(1001, "成功", "success"),
    UNAUTHORIZED(1002, "未登录或令牌失效", "not logged in or token expired"),
    FORBIDDEN(4003, "权限不足", "permission denied"),
    NETWORK_BUSY(4004, "网络繁忙，请稍后重试", "network is busy. please try again later"),
    PHONE_ALREADY_EXISTS(2001, "该手机号已注册", "this phone number is already registered"),
    PHONE_NOT_EXISTS(2002, "该手机号尚未注册", "this phone number has not registered yet"),
    PHONE_OR_PASSWORD_ERROR(2003, "手机号或密码错误", "incorrect phone number or password"),
    COVER_IMAGES_REQUIRED(2004, "封面图不能为空", "cover images cannot be empty"),
    CATEGORY_HAS_ITEMS(2005, "当前分类下有商品，不能删除", "category has items, cannot be deleted"),
    ITEM_NAME_REQUIRED(2006, "商品名不能为空", "item name cannot be empty"),
    ITEM_NAME_EXCEED_50(2007, "商品名长度超过50", "item name length cannot exceed 50"),
    ITEM_PRICE_REQUIRED(2008, "商品价格不能为空", "item price cannot be empty"),
    INVALID_ITEM_PRICE(2009,"商品价格不能小于0","price cannot be negative"),
    ITEM_INTRODUCTION_REQUIRED(2010, "商品介绍不能为空", "item introduction cannot be empty"),
    ITEM_INTRODUCTION_EXCEED_2000(2011, "商品介绍长度超过50", "item introduction length cannot exceed 2000"),
    CATEGORY_NOT_EXISTS(2012,"分类不存在","category not exist"),

    ITEM_CREATE_FAILED(2013,"商品创建失败","create item failed"),
    ITEM_NOT_EXISTS(2014,"商品不存在","item not exist"),
    ITEM_UPDATE_FAILED(2015,"商品更新失败","update item failed"),

    FILE_REQUIRED(2016,"文件不能为空","file cannot be empty"),
    FILE_NOT_ZIP(2017,"文件不是zip格式","file is not a ZIP file"),

    EXCEL_IMPORT_FAILED(2018,"excel文件导入失败","excel file import failed"),

    CATEGORY_ALREADY_DELETED(2019,"分类已经被删除","category is already deleted"),
    ITEM_ALREADY_DELETED(2020,"商品已经被删除","item is already deleted")
    ;

    private final int code;
    private final String msg;
    private final String logMsg;

    ResponseCodeEnum(int code, String msg, String logMsg) {
        this.code = code;
        this.msg = msg;
        this.logMsg = logMsg;
    }
}
