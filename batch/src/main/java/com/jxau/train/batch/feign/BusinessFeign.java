package com.jxau.train.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "business", url = "http://localhost:8002/business")
public interface BusinessFeign {

    @GetMapping("/hello")
    String hello();
}
