package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainTicketQueryReq;
import com.jxau.train.business.req.DailyTrainTicketSaveReq;
import com.jxau.train.business.resp.DailyTrainTicketQueryResp;

public interface DailyTrainTicketService {
    //新增乘车人信息
    void save(DailyTrainTicketSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
