package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.DailyTrainTicket;
import com.jxau.train.business.enums.ConfirmOrderStatusEnum;
import com.jxau.train.business.service.DailyTrainTicketService;
import com.jxau.train.common.context.LoginMemberContext;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.ConfirmOrder;
import com.jxau.train.business.domain.ConfirmOrderExample;
import com.jxau.train.business.mapper.ConfirmOrderMapper;
import com.jxau.train.business.req.ConfirmOrderQueryReq;
import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.resp.ConfirmOrderQueryResp;
import com.jxau.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderServiceImpl.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Override
    public void save(ConfirmOrderDoReq req) {
        Date now = new Date();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if(ObjectUtil.isNull(confirmOrder.getId())){
            //使用雪花算法生成ID
            confirmOrder.setId(SnowUtil.getSnowFlakeId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        }else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    @Override
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {

        //mybatis条件查询类
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        //创建条件
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();


        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);
        //分页结果
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<ConfirmOrderQueryResp> confirmOrderQueryResp = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        //封装分页结果
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(confirmOrderQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void doConfirm(ConfirmOrderDoReq req) {
        //省略业务数据校验，如:车次是否存在，余票是否存在，车次是否在有效期内，ticket是否大于等于0，同车次同车票不能重复购买

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        //保存确认订单表，状态初始
        Date now = new Date();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowFlakeId());
        confirmOrder.setMemberId(LoginMemberContext.getId());


        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setTickets(JSON.toJSONString(req.getTickets()));

        confirmOrderMapper.insert(confirmOrder);
        //查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);
        //扣减余票数量，并判断余票是否充足

        //选座

            //一个车厢的一个车厢的获取座位数据

            //挑选符合条件的座位，如果这个车厢不满足，则进入下个车厢（多个座位应该安排在同一个车厢）

        //选中座位后事务处理

            //座位表的修改售卖情况sell:

            //余票详情表修改余票

            //为会员增加购票记录

            //更新确认订单成功
    }
}
