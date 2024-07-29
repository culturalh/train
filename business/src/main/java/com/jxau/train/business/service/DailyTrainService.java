package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainQueryReq;
import com.jxau.train.business.req.DailyTrainSaveReq;
import com.jxau.train.business.resp.DailyTrainQueryResp;

public interface DailyTrainService {
    //新增乘车人信息
    void save(DailyTrainSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
