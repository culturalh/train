package com.jxau.train.member.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.member.req.PassengerQueryReq;
import com.jxau.train.member.req.PassengerSaveReq;
import com.jxau.train.member.resp.PassengerQueryResp;

public interface PassengerService {
    //新增乘车人信息
    void save(PassengerSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<PassengerQueryResp> queryList(PassengerQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
