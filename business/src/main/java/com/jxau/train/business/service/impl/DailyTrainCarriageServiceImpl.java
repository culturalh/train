package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.enums.SeatColEnum;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.DailyTrainCarriage;
import com.jxau.train.business.domain.DailyTrainCarriageExample;
import com.jxau.train.business.mapper.DailyTrainCarriageMapper;
import com.jxau.train.business.req.DailyTrainCarriageQueryReq;
import com.jxau.train.business.req.DailyTrainCarriageSaveReq;
import com.jxau.train.business.resp.DailyTrainCarriageQueryResp;
import com.jxau.train.business.service.DailyTrainCarriageService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainCarriageServiceImpl implements DailyTrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageServiceImpl.class);

    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Override
    public void save(DailyTrainCarriageSaveReq req) {
        Date now = new Date();
        List<SeatColEnum> colsByType = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(colsByType.size());
        req.setSeatCount(req.getRowCount() * req.getColCount());
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            dailyTrainCarriage.setId(SnowUtil.getSnowFlakeId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);
        }
    }

    @Override
    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {

        //mybatis条件查询类
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.setOrderByClause("train_code desc,carriage_index asc,carriage_seat_index asc");
        //创建条件
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();

        //创建条件
        if(ObjectUtil.isNotNull(req.getDate())){
            criteria.andDateEqualTo(req.getDate());
        }
        //创建条件
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
        //分页结果
        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DailyTrainCarriageQueryResp> dailyTrainCarriageQueryResp = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryResp.class);

        //封装分页结果
        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dailyTrainCarriageQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }
}
