package com.jxau.train.business.service.impl;


import com.jxau.train.business.domain.*;

import com.jxau.train.business.mapper.DailyTrainSeatMapper;
import com.jxau.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.jxau.train.business.service.*;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderServiceImpl implements AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderServiceImpl.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    @Transactional
    @Override
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket,List<DailyTrainSeat> finalSeatList) {
        for (DailyTrainSeat dailyTrainSeat:finalSeatList) {
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

            //计算这个站卖出去后，影响了哪些站的余票库存
            //影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            //假设10个站，本次买4~7站
            //原售：001000001
            //现售：000011100
            //新售：001011101
            //影响：XXX11111X
//            Integer startIndex = 4;
//            Integer endIndex = 7;
//            Integer minStartIndex = startIndex - 往前碰到的最后一个0;
//            Integer maxStartIndex = endIndex -1;
//            Integer minEndIndex = startIndex + 1;
//            Integer maxEndIndex = endIndex + 往后碰到的最后一个0;

            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = dailyTrainSeat.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex + 1;
            Integer minStartIndex = 0;
            for (int i = startIndex -1 ; i >= 0; i--) {
                if (chars[i] == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            LOG.info("影响的出发站的区间{}-{}",minStartIndex,maxStartIndex);
            Integer maxEndIndex = seatForUpdate.getSell().length();
            for (int i = endIndex ; i < seatForUpdate.getSell().length(); i++) {
                if (chars[i] == '1') {
                    maxEndIndex = i ;
                    break;
                }
            }
            LOG.info("影响的到达中的区间{}-{}",minEndIndex,maxEndIndex);
            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);
        }

    }

}
