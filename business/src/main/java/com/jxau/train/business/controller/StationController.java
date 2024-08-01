package com.jxau.train.business.controller;

import com.jxau.train.business.req.StationQueryReq;
import com.jxau.train.business.req.StationSaveReq;
import com.jxau.train.business.resp.StationQueryResp;
import com.jxau.train.business.service.StationService;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;


    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req)
    {
        PageResp<StationQueryResp> list = stationService.queryList(req);
        return new CommonResp<>(list);
    }


    //查询所有车次信息
    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }
}
