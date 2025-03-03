package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.Train;
import com.jxau.train.business.domain.TrainExample;
import com.jxau.train.business.resp.TrainQueryResp;
import com.jxau.train.common.exception.BusinessException;
import com.jxau.train.common.exception.BusinessExceptionEnum;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.Station;
import com.jxau.train.business.domain.StationExample;
import com.jxau.train.business.mapper.StationMapper;
import com.jxau.train.business.req.StationQueryReq;
import com.jxau.train.business.req.StationSaveReq;
import com.jxau.train.business.resp.StationQueryResp;
import com.jxau.train.business.service.StationService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationServiceImpl.class);

    @Resource
    private StationMapper stationMapper;

    @Override
    public void save(StationSaveReq req) {

        Date now = new Date();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if(ObjectUtil.isNull(req.getId())){

            //创建条件
            Station stationDB = selectByUnique(req.getName());
            if(ObjectUtil.isNotEmpty(stationDB)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
            }

            //使用雪花算法生成ID
            station.setId(SnowUtil.getSnowFlakeId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        }else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    private Station selectByUnique(String name) {
        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
        criteria.andNameEqualTo(name);
        List<Station> stations = stationMapper.selectByExample(stationExample);
        if(CollUtil.isNotEmpty(stations)){
            return stations.get(0);
        }else {
            return null;
        }
    }

    @Override
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {

        //mybatis条件查询类
        StationExample stationExample = new StationExample();
        //创建条件
        StationExample.Criteria criteria = stationExample.createCriteria();


        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Station> stationList = stationMapper.selectByExample(stationExample);
        //分页结果
        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<StationQueryResp> stationQueryResp = BeanUtil.copyToList(stationList, StationQueryResp.class);

        //封装分页结果
        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(stationQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<StationQueryResp> queryAll() {
        //mybatis条件查询类
        StationExample trainExample = new StationExample();
        trainExample.setOrderByClause("name_pinyin asc");
        List<Station> trainList = stationMapper.selectByExample(trainExample);
        List<StationQueryResp> trainQueryResp = BeanUtil.copyToList(trainList, StationQueryResp.class);
        return trainQueryResp;
    }
}
