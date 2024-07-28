package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.enums.SeatColEnum;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.TrainCarriage;
import com.jxau.train.business.domain.TrainCarriageExample;
import com.jxau.train.business.mapper.TrainCarriageMapper;
import com.jxau.train.business.req.TrainCarriageQueryReq;
import com.jxau.train.business.req.TrainCarriageSaveReq;
import com.jxau.train.business.resp.TrainCarriageQueryResp;
import com.jxau.train.business.service.TrainCarriageService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainCarriageServiceImpl implements TrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageServiceImpl.class);

    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    @Override
    public void save(TrainCarriageSaveReq req) {
        Date now = new Date();
        List<SeatColEnum> colsByType = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(colsByType.size());
        req.setSeatCount(req.getRowCount() * req.getColCount());
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            trainCarriage.setId(SnowUtil.getSnowFlakeId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        }else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }
    }

    @Override
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {

        //mybatis条件查询类
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("train_code asc, `index` asc");
        //创建条件
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);
        //分页结果
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainCarriageQueryResp> trainCarriageQueryResp = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);

        //封装分页结果
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(trainCarriageQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

    //根据trainCode 查询车次车厢
    @Override
    public List<TrainCarriage> selectByTrainCode(String trainCode){
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("`index` asc");
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        return trainCarriageMapper.selectByExample(trainCarriageExample);
    }

}
