package com.jxau.train.member.controller.admin;

import com.jxau.train.common.context.LoginMemberContext;
import com.jxau.train.common.resp.CommonResp;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.member.req.TicketQueryReq;
import com.jxau.train.member.resp.TicketQueryResp;
import com.jxau.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Resource
    private TicketService ticketService;

//    @PostMapping("/save")
//    public CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req)
//    {
//        ticketService.save(req);
//        return new CommonResp<>();
//    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req)
    {
//        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketQueryResp> list = ticketService.queryList(req);

        return new CommonResp<>(list);
    }

//    @DeleteMapping("/delete/{id}")
//    public CommonResp<Object> delete(@PathVariable Long id)
//    {
//        ticketService.delete(id);
//        return new CommonResp<>();
//    }
}
