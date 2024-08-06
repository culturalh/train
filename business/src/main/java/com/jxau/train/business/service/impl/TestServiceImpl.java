package com.jxau.train.business.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.jxau.train.business.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Override
    @SentinelResource(value = "hello2")
    public void hello2() throws InterruptedException {
            Thread.sleep(1000);
    }
}
