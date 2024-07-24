package com.jxau.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.member.domain.Passenger;
import com.jxau.train.member.mapper.PassengerMapper;
import com.jxau.train.member.req.PassengerSaveReq;
import com.jxau.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PassengerServiceImpl implements PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq req) {
        Date now = new Date();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setId(SnowUtil.getSnowFlakeId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }
}
