package com.jxau.train.business.controller;

import com.jxau.train.business.req.TrainQueryReq;
import com.jxau.train.business.req.TrainSaveReq;
import com.jxau.train.business.resp.TrainQueryResp;
import com.jxau.train.business.service.TrainSeatService;
import com.jxau.train.business.service.TrainService;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;



    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req)
    {
        PageResp<TrainQueryResp> list = trainService.queryList(req);
        return new CommonResp<>(list);
    }



    //查询所有车次信息
    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll(){
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }


}
