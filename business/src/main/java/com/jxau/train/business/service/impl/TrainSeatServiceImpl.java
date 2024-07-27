package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.TrainSeat;
import com.jxau.train.business.domain.TrainSeatExample;
import com.jxau.train.business.mapper.TrainSeatMapper;
import com.jxau.train.business.req.TrainSeatQueryReq;
import com.jxau.train.business.req.TrainSeatSaveReq;
import com.jxau.train.business.resp.TrainSeatQueryResp;
import com.jxau.train.business.service.TrainSeatService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatServiceImpl.class);

    @Resource
    private TrainSeatMapper trainSeatMapper;

    @Override
    public void save(TrainSeatSaveReq req) {
        Date now = new Date();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            trainSeat.setId(SnowUtil.getSnowFlakeId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        }else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {

        //mybatis条件查询类
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        //创建条件
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();


        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample);
        //分页结果
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeatList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainSeatQueryResp> trainSeatQueryResp = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);

        //封装分页结果
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(trainSeatQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }
}
