package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.ConfirmOrderQueryReq;
import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.resp.ConfirmOrderQueryResp;

public interface ConfirmOrderService {
    //新增乘车人信息
    void save(ConfirmOrderDoReq req);

    //根据会员id查询乘车人信息
    PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //确认订单
    void doConfirm(ConfirmOrderDoReq req);
}
