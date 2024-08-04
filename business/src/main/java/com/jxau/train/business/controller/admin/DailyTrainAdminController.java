package com.jxau.train.business.controller.admin;

import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainQueryReq;
import com.jxau.train.business.req.DailyTrainSaveReq;
import com.jxau.train.business.resp.DailyTrainQueryResp;
import com.jxau.train.business.service.DailyTrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req)
    {
        dailyTrainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req)
    {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
        return new CommonResp<>(list);
    }



    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    /**
     * 生成每日车次数据
     * @param date
     * @return
     */
    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date)
    {
//        dailyTrainService.delete(id);
        dailyTrainService.genDaily(date);
        return new CommonResp<>();
    }
}
