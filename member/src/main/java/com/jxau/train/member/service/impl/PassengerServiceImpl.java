package com.jxau.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.jxau.train.common.context.LoginMemberContext;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.member.domain.Passenger;
import com.jxau.train.member.domain.PassengerExample;
import com.jxau.train.member.mapper.PassengerMapper;
import com.jxau.train.member.req.PassengerQueryReq;
import com.jxau.train.member.req.PassengerSaveReq;
import com.jxau.train.member.resp.PassengerQueryResp;
import com.jxau.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq req) {
        Date now = new Date();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        //使用雪花算法生成ID
        passenger.setId(SnowUtil.getSnowFlakeId());
        //从上下文本地变量中获取登录用户会员ID
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }

    @Override
    public List<PassengerQueryResp>  queryList(PassengerQueryReq req) {

        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        PageHelper.startPage(2,2);
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
        List<PassengerQueryResp> passengerQueryResp = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
        return passengerQueryResp;
    }
}
