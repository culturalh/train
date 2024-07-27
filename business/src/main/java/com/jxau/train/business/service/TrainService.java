package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.TrainQueryReq;
import com.jxau.train.business.req.TrainSaveReq;
import com.jxau.train.business.resp.TrainQueryResp;

public interface TrainService {
    //新增乘车人信息
    void save(TrainSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<TrainQueryResp> queryList(TrainQueryReq req);

    //根据会员id删除
    void delete(Long id);
}
