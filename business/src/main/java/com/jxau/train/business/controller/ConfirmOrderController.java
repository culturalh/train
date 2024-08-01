package com.jxau.train.business.controller;

import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.req.ConfirmOrderQueryReq;
import com.jxau.train.business.resp.ConfirmOrderQueryResp;
import com.jxau.train.business.service.ConfirmOrderService;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req)
    {
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }

}
