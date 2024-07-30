package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainQueryReq;
import com.jxau.train.business.req.DailyTrainSaveReq;
import com.jxau.train.business.resp.DailyTrainQueryResp;

import java.util.Date;

public interface DailyTrainService {
    //新增乘车人信息
    void save(DailyTrainSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //生成每日车次信息
    public void genDaily(Date date);
}
