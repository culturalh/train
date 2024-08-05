package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.domain.*;
import com.jxau.train.business.enums.ConfirmOrderStatusEnum;
import com.jxau.train.business.enums.SeatColEnum;
import com.jxau.train.business.enums.SeatTypeEnum;
import com.jxau.train.business.req.ConfirmOrderTicketReq;
import com.jxau.train.business.service.*;
import com.jxau.train.common.context.LoginMemberContext;
import com.jxau.train.common.exception.BusinessException;
import com.jxau.train.common.exception.BusinessExceptionEnum;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.mapper.ConfirmOrderMapper;
import com.jxau.train.business.req.ConfirmOrderQueryReq;
import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.resp.ConfirmOrderQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderServiceImpl.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;


    @Override
    public void save(ConfirmOrderDoReq req) {
        Date now = new Date();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            //使用雪花算法生成ID
            confirmOrder.setId(SnowUtil.getSnowFlakeId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
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
        PageHelper.startPage(req.getPage(), req.getSize());
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
    public synchronized void doConfirm(ConfirmOrderDoReq req) {
        //省略业务数据校验，如:车次是否存在，余票是否存在，车次是否在有效期内，ticket是否大于等于0，同车次同车票不能重复购买

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
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

        confirmOrder.setTickets(JSON.toJSONString(tickets));

        confirmOrderMapper.insert(confirmOrder);
        //查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        //预扣减余票数量，并判断余票是否充足
        reduceTickets(req, dailyTrainTicket);
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();
        //判断是否有选座
        ConfirmOrderTicketReq ticket0= tickets.get(0);
        if(StrUtil.isNotBlank(ticket0.getSeat())){
            LOG.info("本次购票有选座");
            //本次座位类型包含的列
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticket0.getSeatTypeCode());
            //选座
            //计算相对第一个座位的偏移值
            //比如选择的是C1，D2(则偏移值是[0,5])
            //比如选择的是A1,B1,C1(则偏移值是[0,1,2])

            LOG.info("本次座位类型包含的列：{}", colEnumList);
            //组成两排列表 {A1,C1,D1,F1,A2,C2,D2,F2}
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum:colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("用于做参照的两排座位：{}", referSeatList);

            List<Integer> offSeatList = new ArrayList<>();
            List<Integer> absoluteSeatList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticket:tickets) {
                int index = referSeatList.indexOf(ticket.getSeat());
                absoluteSeatList.add(index);
            }
            LOG.info("计算得到两排座位的绝对偏移值：{}", absoluteSeatList);
            for ( Integer index : absoluteSeatList){
                int offset = index - absoluteSeatList.get(0);
                offSeatList.add(offset);
            }
            LOG.info("计算得到两排座位的相对第一个座位偏移值：{}", offSeatList);
            
            getSeat(finalSeatList,
                    date,
                    trainCode,
                    ticket0.getSeatTypeCode(),
                    ticket0.getSeat().split("")[0],
                    offSeatList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex());

        }else {
            LOG.info("本次购票无选座");
            for (ConfirmOrderTicketReq confirmOrderTicketReq : tickets) {
                getSeat( finalSeatList,date,trainCode,confirmOrderTicketReq.getSeatTypeCode(),null,null,dailyTrainTicket.getStartIndex(),dailyTrainTicket.getEndIndex());

            }
        }


        LOG.info("最终选择的座位：{}", finalSeatList);


        try {
            afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,finalSeatList, tickets, confirmOrder);
        } catch (Exception e) {
            LOG.info("保存购票信息失败: {}",e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
//            e.printStackTrace();
        }


    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell= 10001 本次购买区间站1~4，则区间以售卖000
     * 全部是0，标识整个区间可买：只要有1，就表示区间内已售卖过票
     *
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位或，最终得到11111
     * @param dailyTrainSeat
     * @param startIndex
     * @param endIndex
     */
    private Boolean calSell(DailyTrainSeat dailyTrainSeat,Integer startIndex,Integer endIndex){
        String sell = dailyTrainSeat.getSell();
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0){
            LOG.info("座位{}在本车站区间{}~{}已被售卖", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        }else{
            LOG.info("座位{}在本车站区间{}~{}可以售卖", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            String curSell = sellPart.replace("0", "1");
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());
            //当前售票信息与库里的售票信息进行按位或，获取新的售票信息
            int newSellInt = NumberUtil.binaryToInt(curSell) | Integer.parseInt(sellPart);
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息{}，本车站区间{}~{}，即{} ，最终售票信息：{}", dailyTrainSeat.getCarriageSeatIndex(), sell,startIndex, endIndex,curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }
    }

    /**
     * 如果有选座则一次性选完，否则一个一个选
     * @param date
     * @param trainCode
     * @param seatType
     * @param column
     * @param offSeatList
     */
    private void getSeat( List<DailyTrainSeat> finalSeatList,Date date,String trainCode,String seatType,String column, List<Integer> offSeatList,Integer startIndex,Integer endIndex){
        List<DailyTrainCarriage> trainCarriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        LOG.info("共查出{}个符合条件的车厢", trainCarriageList.size());
        //挑选符合条件的座位，如果这个车厢不满足，则进入下个车厢（多个座位应该安排在同一个车厢）
        //一个车厢的一个车厢的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : trainCarriageList) {

            getSeatList = new ArrayList<>();
            //查出车厢所有座位
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());

            LOG.info("车厢{}共查出{}个座位", dailyTrainCarriage.getIndex(), seatList.size());
            //遍历座位
         LOG.info("车厢{}座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (int i = 0, seatListSize = seatList.size(); i < seatListSize; i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                String col = dailyTrainSeat.getCol();
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();


                //判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if(finalSeat.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag){
                    LOG.info("座位{}已被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }
                //需要判断colume,有值的话需要比对列号
                if (StrUtil.isBlank(column)) {
                    LOG.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }
                Boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
//                    LOG.info("未选中座位");
                    continue;
                }
                //根据offSetList选剩下的座位

                boolean isGetAllOffSetSeat = true;
                if (CollUtil.isNotEmpty(offSeatList)) {
                    //有偏移值
                    LOG.info("座位{}有偏移值，校验偏移座位是否可选", seatIndex);
                    for (int j = 1; j < offSeatList.size(); j++) {
                        Integer offset = offSeatList.get(j);
                        //座位在库里的索引是从1开始
//                        int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset ;

                        //有选座时，一定实在同一个车厢
                        if (nextIndex >= seatList.size()) {
                            LOG.info("座位{}不可选，偏移后的座位超出了这个车厢的座位数", nextIndex);
                            isGetAllOffSetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        Boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffSetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffSetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                //保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }
    private void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum){

                case YDZ ->{
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (dailyTrainTicket.getYdz() < 1){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ ->{
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (dailyTrainTicket.getEdz() < 1){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case RW ->{
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (dailyTrainTicket.getRw() < 1){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case YW ->{
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (dailyTrainTicket.getYw() < 1){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }

            }
        }
    }
}
