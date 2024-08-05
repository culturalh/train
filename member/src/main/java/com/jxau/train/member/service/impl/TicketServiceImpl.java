package com.jxau.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.req.MemberTicketReq;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.member.domain.Ticket;
import com.jxau.train.member.domain.TicketExample;
import com.jxau.train.member.mapper.TicketMapper;
import com.jxau.train.member.req.TicketQueryReq;
import com.jxau.train.member.resp.TicketQueryResp;
import com.jxau.train.member.service.TicketService;
import io.seata.core.context.RootContext;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Resource
    private TicketMapper ticketMapper;

    @Override
    public void save(MemberTicketReq req) throws Exception {
        LOG.info("seata全局事务ID{}", RootContext.getXID());
        Date now = new Date();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        //使用雪花算法生成ID
        ticket.setId(SnowUtil.getSnowFlakeId());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
        //模拟被调用方法出现异常
//        if(1 == 1){
//            throw new Exception("测试异常11");
//        }
    }

    @Override
    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {

        //mybatis条件查询类
        TicketExample ticketExample = new TicketExample();
        //创建条件
        TicketExample.Criteria criteria = ticketExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);
        //分页结果
        PageInfo<Ticket> pageInfo = new PageInfo<>(ticketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TicketQueryResp> ticketQueryResp = BeanUtil.copyToList(ticketList, TicketQueryResp.class);

        //封装分页结果
        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(ticketQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}
