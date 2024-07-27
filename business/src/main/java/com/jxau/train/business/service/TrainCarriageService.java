package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.TrainCarriageQueryReq;
import com.jxau.train.business.req.TrainCarriageSaveReq;
import com.jxau.train.business.resp.TrainCarriageQueryResp;

public interface TrainCarriageService {
    //新增乘车人信息
    void save(TrainCarriageSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
