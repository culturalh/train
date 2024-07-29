package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.DailyTrainSeat;
import com.jxau.train.business.domain.DailyTrainSeatExample;
import com.jxau.train.business.mapper.DailyTrainSeatMapper;
import com.jxau.train.business.req.DailyTrainSeatQueryReq;
import com.jxau.train.business.req.DailyTrainSeatSaveReq;
import com.jxau.train.business.resp.DailyTrainSeatQueryResp;
import com.jxau.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatServiceImpl.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

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
        dailyTrainSeatExample.setOrderByClause("train_code desc,carriage_index asc,carriage_seat_index asc");
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
}
