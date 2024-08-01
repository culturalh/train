package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainSeatQueryReq;
import com.jxau.train.business.req.DailyTrainSeatSaveReq;
import com.jxau.train.business.resp.DailyTrainSeatQueryResp;

import java.util.Date;

public interface DailyTrainSeatService {
    //新增乘车人信息
    void save(DailyTrainSeatSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //生成某日某车次的座位信息
    void genDaily(Date date, String trainCode);

    //查询某日某车次的某种座位的数量信息
    int countSeat(Date date,String trainCode,String seatType);
}
