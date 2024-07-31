package com.jxau.train.business.service;

import com.jxau.train.business.domain.TrainStation;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.TrainStationQueryReq;
import com.jxau.train.business.req.TrainStationSaveReq;
import com.jxau.train.business.resp.TrainStationQueryResp;

import java.util.List;

public interface TrainStationService {
    //新增乘车人信息
    void save(TrainStationSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req);

    //根据会员id删除
    void delete(Long id);

    List<TrainStation> selectByTrainCode(String trainCode);
}
