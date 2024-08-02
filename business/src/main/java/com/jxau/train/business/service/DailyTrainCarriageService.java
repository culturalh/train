package com.jxau.train.business.service;

import com.jxau.train.business.domain.DailyTrainCarriage;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.DailyTrainCarriageQueryReq;
import com.jxau.train.business.req.DailyTrainCarriageSaveReq;
import com.jxau.train.business.resp.DailyTrainCarriageQueryResp;

import java.util.Date;
import java.util.List;

public interface DailyTrainCarriageService {
    //新增乘车人信息
    void save(DailyTrainCarriageSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //生成每日车次的车厢数据
    void genDaily(Date date, String trainCode);

    //根据日期车次和座位类型查询
    List<DailyTrainCarriage> selectBySeatType(Date date, String trainCode, String seatType);
}
