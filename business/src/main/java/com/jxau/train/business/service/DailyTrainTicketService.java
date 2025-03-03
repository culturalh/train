package com.jxau.train.business.service;

import com.jxau.train.business.domain.DailyTrain;
import com.jxau.train.business.domain.DailyTrainTicket;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainTicketQueryReq;
import com.jxau.train.business.req.DailyTrainTicketSaveReq;
import com.jxau.train.business.resp.DailyTrainTicketQueryResp;

import java.util.Date;

public interface DailyTrainTicketService {
    //新增乘车人信息
    void save(DailyTrainTicketSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);

    PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq req);

    PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //生成每日车次的所有余票信息
    void genDaily(DailyTrain dailyTrain, Date date, String trainCode);

    //根据车次和日期查询余票信息
    DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end);
}
