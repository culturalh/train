package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainStationQueryReq;
import com.jxau.train.business.req.DailyTrainStationSaveReq;
import com.jxau.train.business.resp.DailyTrainStationQueryResp;

public interface DailyTrainStationService {
    //新增乘车人信息
    void save(DailyTrainStationSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
