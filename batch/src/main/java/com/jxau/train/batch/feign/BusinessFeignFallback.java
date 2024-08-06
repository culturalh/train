package com.jxau.train.batch.feign;

import com.jxau.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class BusinessFeignFallback implements BusinessFeign{
    @Override
    public String hello() {
        return "fallback";
    }

    @Override
    public CommonResp<Object> genDaily(Date date) {
        return null;
    }
}
