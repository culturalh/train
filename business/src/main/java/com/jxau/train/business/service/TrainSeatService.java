package com.jxau.train.business.service;

import com.jxau.train.business.domain.TrainSeat;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.TrainSeatQueryReq;
import com.jxau.train.business.req.TrainSeatSaveReq;
import com.jxau.train.business.resp.TrainSeatQueryResp;

import java.util.List;

public interface TrainSeatService {
    //新增乘车人信息
    void save(TrainSeatSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //生成座位数
    void genTrainSeat(String trainCode);

    //根据车次查询
    List<TrainSeat> selectByTrainCode(String trainCode);
}
