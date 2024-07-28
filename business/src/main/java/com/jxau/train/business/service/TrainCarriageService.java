package com.jxau.train.business.service;

import com.jxau.train.business.domain.TrainCarriage;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.TrainCarriageQueryReq;
import com.jxau.train.business.req.TrainCarriageSaveReq;
import com.jxau.train.business.resp.TrainCarriageQueryResp;

import java.util.List;

public interface TrainCarriageService {
    //新增乘车人信息
    void save(TrainCarriageSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //根据车次查询
    List<TrainCarriage> selectByTrainCode(String trainCode);
}
