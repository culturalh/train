package com.jxau.train.${module}.service;

import com.jxau.train.common.resp.PageResp;
import com.jxau.train.${module}.req.${Domain}QueryReq;
import com.jxau.train.${module}.req.${Domain}SaveReq;
import com.jxau.train.${module}.resp.${Domain}QueryResp;

public interface ${Domain}Service {
    //新增乘车人信息
    void save(${Domain}SaveReq req);

    //根据会员id查询乘车人信息
    PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req);

    //根据会员id删除
    void delete(Long id);
}
