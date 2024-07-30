package com.jxau.train.batch.controller;

import com.jxau.train.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @Resource
    private BusinessFeign businessFeign;

    @GetMapping("/hello")
    public String hello(){
        businessFeign.hello();
        return "hello world!Batch!";
    }
}
