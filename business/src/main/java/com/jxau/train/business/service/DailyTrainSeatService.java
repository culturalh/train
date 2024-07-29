package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainSeatQueryReq;
import com.jxau.train.business.req.DailyTrainSeatSaveReq;
import com.jxau.train.business.resp.DailyTrainSeatQueryResp;

public interface DailyTrainSeatService {
    //新增乘车人信息
    void save(DailyTrainSeatSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
