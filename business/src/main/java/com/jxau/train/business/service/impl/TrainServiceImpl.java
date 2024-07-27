package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.Train;
import com.jxau.train.business.domain.TrainExample;
import com.jxau.train.business.mapper.TrainMapper;
import com.jxau.train.business.req.TrainQueryReq;
import com.jxau.train.business.req.TrainSaveReq;
import com.jxau.train.business.resp.TrainQueryResp;
import com.jxau.train.business.service.TrainService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainServiceImpl implements TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainServiceImpl.class);

    @Resource
    private TrainMapper trainMapper;

    @Override
    public void save(TrainSaveReq req) {
        Date now = new Date();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            train.setId(SnowUtil.getSnowFlakeId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        }else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    @Override
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {

        //mybatis条件查询类
        TrainExample trainExample = new TrainExample();
        //创建条件
        TrainExample.Criteria criteria = trainExample.createCriteria();


        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        //分页结果
        PageInfo<Train> pageInfo = new PageInfo<>(trainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainQueryResp> trainQueryResp = BeanUtil.copyToList(trainList, TrainQueryResp.class);

        //封装分页结果
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(trainQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }


    @Override
    public List<TrainQueryResp> queryAll() {
        //mybatis条件查询类
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code desc");
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        List<TrainQueryResp> trainQueryResp = BeanUtil.copyToList(trainList, TrainQueryResp.class);
        return trainQueryResp;
    }
}
