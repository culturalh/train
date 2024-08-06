package com.jxau.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.req.ConfirmOrderQueryReq;
import com.jxau.train.business.resp.ConfirmOrderQueryResp;
import com.jxau.train.business.service.ConfirmOrderService;
import com.jxau.train.business.service.impl.AfterConfirmOrderServiceImpl;
import com.jxau.train.common.exception.BusinessException;
import com.jxau.train.common.exception.BusinessExceptionEnum;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderController.class);


    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/do")
    @SentinelResource(value="/confirmOrderDo",blockHandler = "doConfirmBlockHandler")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req)
    {
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }
    /**
     * 需包含原方法所有参数和BlockException参数,返回值也需要一致
     * @param req
     * @param e
     */
    public CommonResp<Object> doConfirmBlockHandler(ConfirmOrderDoReq req, BlockException e){
        LOG.info("购票请求被限流{}",req);
//        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
        CommonResp<Object> commonResp = new CommonResp<>();
        commonResp.setSuccess(false);
        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
        return commonResp;
    }
}
