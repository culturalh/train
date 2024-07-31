package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.*;
import com.jxau.train.business.mapper.TrainStationMapper;
import com.jxau.train.business.service.TrainStationService;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.mapper.DailyTrainStationMapper;
import com.jxau.train.business.req.DailyTrainStationQueryReq;
import com.jxau.train.business.req.DailyTrainStationSaveReq;
import com.jxau.train.business.resp.DailyTrainStationQueryResp;
import com.jxau.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainStationServiceImpl implements DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationServiceImpl.class);

    @Resource
    private DailyTrainStationMapper dailyTrainStationMapper;

    @Resource
    private TrainStationService trainStationService;

    @Override
    public void save(DailyTrainStationSaveReq req) {
        Date now = new Date();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (ObjectUtil.isNull(req.getId())) {
            //使用雪花算法生成ID
            dailyTrainStation.setId(SnowUtil.getSnowFlakeId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }
    }

    @Override
    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {

        //mybatis条件查询类
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.setOrderByClause("date desc,train_code asc,`index` asc");
        //创建条件
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();

        //创建条件
        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        //创建条件
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        //开始分页
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainStation> dailyTrainStationList = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);
        //分页结果
        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DailyTrainStationQueryResp> dailyTrainStationQueryResp = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryResp.class);

        //封装分页结果
        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dailyTrainStationQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }
    @Transactional
    @Override
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车站站信息开始", DateUtil.formatDate(date), trainCode);
        Date now = new Date();
        //删除已有车次数据
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);
        //先查询所有车次的信息
        List<TrainStation> trainStationList = trainStationService.selectByTrainCode(trainCode);

        if (ObjectUtil.isEmpty(trainStationList)) {
            LOG.info("该车次没有车站基础数据，生成该车次的车站信息结束");
            return;
        }
        //生成改车次已有的数据
        for (TrainStation trainStation : trainStationList) {
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowFlakeId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
        LOG.info("生成日期【{}】车次【{}】的车站站信息结束", DateUtil.formatDate(date), trainCode);
    }
}
