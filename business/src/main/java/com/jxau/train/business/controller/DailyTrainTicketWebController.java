package com.jxau.train.business.controller;

import com.jxau.train.business.req.DailyTrainTicketQueryReq;
import com.jxau.train.business.req.DailyTrainTicketSaveReq;
import com.jxau.train.business.resp.DailyTrainTicketQueryResp;
import com.jxau.train.business.service.DailyTrainTicketService;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketWebController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;


    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req)
    {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}
