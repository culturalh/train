package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.TrainStation;
import com.jxau.train.business.domain.TrainStationExample;
import com.jxau.train.business.mapper.TrainStationMapper;
import com.jxau.train.business.req.TrainStationQueryReq;
import com.jxau.train.business.req.TrainStationSaveReq;
import com.jxau.train.business.resp.TrainStationQueryResp;
import com.jxau.train.business.service.TrainStationService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainStationServiceImpl implements TrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationServiceImpl.class);

    @Resource
    private TrainStationMapper trainStationMapper;

    @Override
    public void save(TrainStationSaveReq req) {
        Date now = new Date();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            trainStation.setId(SnowUtil.getSnowFlakeId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        }else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }
    }

    @Override
    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {

        //mybatis条件查询类
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("train_code asc, `index` asc");
        //创建条件
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);
        //分页结果
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainStationQueryResp> trainStationQueryResp = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);

        //封装分页结果
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(trainStationQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }
}
