package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.*;
import com.jxau.train.business.service.TrainSeatService;
import com.jxau.train.business.service.TrainStationService;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.mapper.DailyTrainSeatMapper;
import com.jxau.train.business.req.DailyTrainSeatQueryReq;
import com.jxau.train.business.req.DailyTrainSeatSaveReq;
import com.jxau.train.business.resp.DailyTrainSeatQueryResp;
import com.jxau.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatServiceImpl.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private TrainSeatService trainSeatService;

    @Resource
    private TrainStationService trainStationService;
    @Override
    public void save(DailyTrainSeatSaveReq req) {
        Date now = new Date();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            dailyTrainSeat.setId(SnowUtil.getSnowFlakeId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }
    }

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {

        //mybatis条件查询类
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("date desc,train_code desc,carriage_index asc,carriage_seat_index asc");
        //创建条件
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        //创建条件
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        //分页结果
        PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(dailyTrainSeatList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DailyTrainSeatQueryResp> dailyTrainSeatQueryResp = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryResp.class);

        //封装分页结果
        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dailyTrainSeatQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);
        Date now = new Date();
        //删除已有车次数据
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);
        //先查询所有车次的信息
        List<TrainSeat> trainSeatList = trainSeatService.selectByTrainCode(trainCode);

        if (ObjectUtil.isEmpty(trainSeatList)) {
            LOG.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        //座位售卖情况
        List<TrainStation> trainStationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0',trainStationList.size()-1);

        //生成改车次已有的数据
        for (TrainSeat trainSeat : trainSeatList) {
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowFlakeId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
        LOG.info("生成日期【{}】车次【{}】的座位站信息结束", DateUtil.formatDate(date), trainCode);
    }

    @Override
    public int countSeat(Date date,String trainCode,String seatType){
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode).andSeatTypeEqualTo(seatType);
        long count= dailyTrainSeatMapper.countByExample(dailyTrainSeatExample);
        if (count==0){
            return -1;
        }
        return (int) count;
    }


    @Override
    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("carriage_seat_index asc");
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andCarriageIndexEqualTo(carriageIndex);
        return dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
    }
}
