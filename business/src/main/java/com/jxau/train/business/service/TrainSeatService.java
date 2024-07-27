package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.TrainSeatQueryReq;
import com.jxau.train.business.req.TrainSeatSaveReq;
import com.jxau.train.business.resp.TrainSeatQueryResp;

public interface TrainSeatService {
    //新增乘车人信息
    void save(TrainSeatSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
