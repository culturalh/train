package com.jxau.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.service.TestService;
import com.jxau.train.common.exception.BusinessException;
import com.jxau.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {
    private static final Logger LOG = LoggerFactory.getLogger(testController.class);

    @Resource
    private TestService testService;
    @GetMapping("/hello")
//    @SentinelResource(value = "hello",blockHandler = "blockHandler")
    @SentinelResource(value = "hello")
    public String hello() throws InterruptedException {

        Thread.sleep(1000);
        testService.hello2();

        return "hello world!Business!Sentinel!";
    }
    /**
     * 需包含原方法所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void doConfirmBlockHandler(ConfirmOrderDoReq req, BlockException e){
        LOG.info("请求被限流{}",req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

}
