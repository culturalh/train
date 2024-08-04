package com.jxau.train.member.service;

import com.jxau.train.common.req.MemberTicketReq;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.member.req.TicketQueryReq;
import com.jxau.train.member.resp.TicketQueryResp;

public interface TicketService {
    //新增乘车人信息
    void save(MemberTicketReq req) throws Exception;

    //根据会员id查询乘车人信息
    PageResp<TicketQueryResp> queryList(TicketQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
