package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.Train;
import com.jxau.train.business.domain.TrainStationExample;
import com.jxau.train.business.service.*;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.DailyTrain;
import com.jxau.train.business.domain.DailyTrainExample;
import com.jxau.train.business.mapper.DailyTrainMapper;
import com.jxau.train.business.req.DailyTrainQueryReq;
import com.jxau.train.business.req.DailyTrainSaveReq;
import com.jxau.train.business.resp.DailyTrainQueryResp;
import jakarta.annotation.Resource;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainServiceImpl implements DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainServiceImpl.class);

    @Resource
    private DailyTrainMapper dailyTrainMapper;

    @Resource
    private TrainService trainService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Override
    public void save(DailyTrainSaveReq req) {
        Date now = new Date();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            dailyTrain.setId(SnowUtil.getSnowFlakeId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        }else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }
    }

    @Override
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {

        //mybatis条件查询类
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc,code asc");
        //创建条件
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();

        //创建条件
        if(ObjectUtil.isNotNull(req.getDate())){
            criteria.andDateEqualTo(req.getDate());
        }
        //创建条件
        if(ObjectUtil.isNotEmpty(req.getCode())){
            criteria.andCodeEqualTo(req.getCode());
        }

        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrain> dailyTrainList = dailyTrainMapper.selectByExample(dailyTrainExample);
        //分页结果
        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DailyTrainQueryResp> dailyTrainQueryResp = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryResp.class);

        //封装分页结果
        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dailyTrainQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void genDaily(Date date) {
        LOG.info("生成日期【{}】车次信息开始", DateUtil.formatDate(date));
        //查询当前车次的信息
        List<Train> trains = trainService.selectAll();

        if(CollUtil.isEmpty(trains)){
            LOG.info("没有车次信息,任务结束");
            return;
        }
        for (Train train : trains) {
            genDailyTrain(date, train);
        }
        LOG.info("生成日期【{}】车次信息结束", DateUtil.formatDate(date));
    }

    public void genDailyTrain(Date date, Train train) {
        Date now = new Date();
        //删除已有车次数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria().andDateEqualTo(date).andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);

        //生成改车次已有的数据
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowFlakeId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        //生成每日车站信息
        dailyTrainStationService.genDaily(date, train.getCode());

        //生成每日车厢信息
        dailyTrainCarriageService.genDaily(date, train.getCode());

        //生成每日座位信息
        dailyTrainSeatService.genDaily(date, train.getCode());
    }


}
