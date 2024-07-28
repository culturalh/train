package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.StationQueryReq;
import com.jxau.train.business.req.StationSaveReq;
import com.jxau.train.business.resp.StationQueryResp;

import java.util.List;

public interface StationService {
    //新增乘车人信息
    void save(StationSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<StationQueryResp> queryList(StationQueryReq req);

    //根据会员id删除
    void delete(Long id);

    //查询所有车站信息
    List<StationQueryResp> queryAll();
}
