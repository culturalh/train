package com.jxau.train.member.controller;

import com.jxau.train.common.context.LoginMemberContext;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.member.req.PassengerQueryReq;
import com.jxau.train.member.req.PassengerSaveReq;
import com.jxau.train.member.resp.PassengerQueryResp;
import com.jxau.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req)
    {
        passengerService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> save(@Valid PassengerQueryReq req)
    {
        //线程本地变量中保存着会员id
        req.setMemberId(LoginMemberContext.getId());
        PageResp<PassengerQueryResp> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {
        passengerService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-mine")
    public CommonResp<List<PassengerQueryResp>> queryAll()
    {
        //线程本地变量中保存着会员id
        List<PassengerQueryResp> list = passengerService.queryMine(LoginMemberContext.getId());
        return new CommonResp<>(list);
    }
}
