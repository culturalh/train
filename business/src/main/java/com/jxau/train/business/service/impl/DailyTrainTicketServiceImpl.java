package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.DailyTrainTicket;
import com.jxau.train.business.domain.DailyTrainTicketExample;
import com.jxau.train.business.mapper.DailyTrainTicketMapper;
import com.jxau.train.business.req.DailyTrainTicketQueryReq;
import com.jxau.train.business.req.DailyTrainTicketSaveReq;
import com.jxau.train.business.resp.DailyTrainTicketQueryResp;
import com.jxau.train.business.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketServiceImpl implements DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketServiceImpl.class);

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Override
    public void save(DailyTrainTicketSaveReq req) {
        Date now = new Date();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            dailyTrainTicket.setId(SnowUtil.getSnowFlakeId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        }else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }
    }

    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {

        //mybatis条件查询类
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        //创建条件
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();


        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        //分页结果
        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DailyTrainTicketQueryResp> dailyTrainTicketQueryResp = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);

        //封装分页结果
        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dailyTrainTicketQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }
}
