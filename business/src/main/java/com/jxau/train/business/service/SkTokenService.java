package com.jxau.train.business.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.business.req.SkTokenQueryReq;
import com.jxau.train.business.req.SkTokenSaveReq;
import com.jxau.train.business.resp.SkTokenQueryResp;

import java.util.Date;

public interface SkTokenService {
    //新增乘车人信息
    void save(SkTokenSaveReq req);

    //根据会员id查询乘车人信息
    PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req);

    //根据会员id删除
    void delete(Long id);


    //校验令牌
    boolean vaildSkToken(Date date, String trainCode,Long memberId);
}
