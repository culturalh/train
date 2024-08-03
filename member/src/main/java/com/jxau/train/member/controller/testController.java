package com.jxau.train.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope//nacos实时刷新
public class testController {

    //注入配置文件的值
    @Value("${test.nacos}")
    private String testNacos;

    @Autowired
    Environment environment;
    @GetMapping("/hello")
    public String hello(){
//        return "hello world";
        return String.format("hello world, %s,端口: %s", testNacos, environment.getProperty("server.port"));

    }
}
