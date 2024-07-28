package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.TrainCarriage;
import com.jxau.train.business.enums.SeatColEnum;
import com.jxau.train.business.enums.SeatTypeEnum;
import com.jxau.train.business.service.TrainCarriageService;
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
import java.util.HashMap;
import java.util.List;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatServiceImpl.class);

    @Resource
    private TrainSeatMapper trainSeatMapper;

    @Resource
    private TrainCarriageService trainCarriageService;

    @Override
    public void save(TrainSeatSaveReq req) {
        Date now = new Date();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(req.getId())) {
            //使用雪花算法生成ID
            trainSeat.setId(SnowUtil.getSnowFlakeId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {

        //mybatis条件查询类
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("train_code asc,carriage_index asc,carriage_seat_index asc");
        //创建条件
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        //开始分页
        PageHelper.startPage(req.getPage(), req.getSize());
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

    @Override
    public void genTrainSeat(String trainCode) {
        Date now = new Date();
        //  清空当前车次下的所有的车厢
        //mybatis条件查询类
        TrainSeatExample trainSeatExample = new TrainSeatExample();//创建条件
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);

        //  查找当前车次下的所有的车厢(这个方法写在车厢实现类之中)
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        //  循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : trainCarriages) {
            LOG.info("当前车厢：{}", trainCarriage);
            //  拿到车厢的数据：行数，座位类型（得到列数）
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            //  根据车厢的座位类型，筛选出所有的列，比如车厢类型是一等座，则筛选出columnList = {ACDF}
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
            LOG.info("当前车厢的座位类型：{}", colEnumList);
            int seatIndex = 1;
            //  循环行数
            for (int row = 1; row <= rowCount; row++) {
                LOG.info("当前车厢的行数：{}", row);
                //  循环列数
                for (SeatColEnum seatColEnum :colEnumList) {
                    LOG.info("当前车厢的行数：{}", seatColEnum);
                    //  构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowFlakeId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }


    }
}
