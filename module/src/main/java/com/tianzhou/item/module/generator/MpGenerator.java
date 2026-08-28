package com.tianzhou.item.module.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class MpGenerator {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/myitem?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
        String user = "root";
        String pwd = "1234";

        //本机电脑磁盘路径
        String basePath = System.getProperty("user.dir").replace("\\", "/");
        String moduleDiskPath = basePath + "/module";
        String consoleDiskPath = basePath + "/console";
        String appDiskPath = basePath + "/app";

        //指定要生成的表
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要生成的表：");
        String input = sc.nextLine().trim();
        // 按逗号拆分，去掉空格
        String[] tables = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        //1、生成 module：entity mapper service xml
        FastAutoGenerator.create(url, user, pwd)
                .globalConfig(builder -> {
                    builder.author("test");
                    builder.disableOpenDir();
                    builder.outputDir(moduleDiskPath + "/src/main/java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.tianzhou.item.module")
                            .entity("entity")
                            .mapper("mapper")
                            .service("service");
                    builder.pathInfo(Collections.singletonMap(OutputFile.xml,
                            moduleDiskPath + "/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables);

                    // entity 模板
                    builder.entityBuilder()
                            .javaTemplate("/templates/entity.java.vm")
                            .enableFileOverride()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel);

                    // mapper + xml 模板 ← 都在这里指定
                    builder.mapperBuilder()
                            .mapperTemplate("/templates/mapper.java.vm")      // mapper 接口模板
                            .mapperXmlTemplate("/templates/mapper.xml.vm")    // mapper xml 模板
                            .enableFileOverride();

                    // service 模板
                    builder.serviceBuilder()
                            .serviceTemplate("/templates/service.java.vm")
                            .enableFileOverride()
                            .formatServiceFileName("%sService")
                            .disableServiceImpl();

                    builder.controllerBuilder().disable();
                })
                .execute();


        //2、生成 console controller
        FastAutoGenerator.create(url, user, pwd)
                .globalConfig(builder -> {
                    builder.author("test");
                    builder.disableOpenDir();
                    builder.outputDir(consoleDiskPath + "/src/main/java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.tianzhou.item.console")
                            .controller("controller");
                })
                .injectionConfig(builder -> {
                    builder.customMap(Collections.singletonMap("modulePkg", "com.tianzhou.item.module"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables);
                    builder.entityBuilder().disable();
                    builder.mapperBuilder().disable();
                    builder.serviceBuilder()
                            .formatServiceFileName("%sService")
                            .disable();
                    builder.controllerBuilder()
                            .template("/templates/controller-console.java.vm")  // ← 移到这里
                            .enableFileOverride();
                })
                .execute();

        //3、生成 app controller
        FastAutoGenerator.create(url, user, pwd)
                .globalConfig(builder -> {
                    builder.author("test");
                    builder.disableOpenDir();
                    builder.outputDir(appDiskPath + "/src/main/java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.tianzhou.item.app")
                            .controller("controller");
                })
                .injectionConfig(builder -> {
                    builder.customMap(Collections.singletonMap("modulePkg", "com.tianzhou.item.module"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables);
                    builder.entityBuilder().disable();
                    builder.mapperBuilder().disable();
                    builder.serviceBuilder()
                            .formatServiceFileName("%sService")
                            .disable();
                    builder.controllerBuilder()
                            .template("/templates/controller-app.java.vm")  // ← 移到这里
                            .enableFileOverride();
                })
                .execute();
    }
}
