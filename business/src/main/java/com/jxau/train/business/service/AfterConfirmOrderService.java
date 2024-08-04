package com.jxau.train.business.service;

import com.jxau.train.business.domain.ConfirmOrder;
import com.jxau.train.business.domain.DailyTrainSeat;
import com.jxau.train.business.domain.DailyTrainTicket;
import com.jxau.train.business.req.ConfirmOrderDoReq;
import com.jxau.train.business.req.ConfirmOrderQueryReq;
import com.jxau.train.business.req.ConfirmOrderTicketReq;
import com.jxau.train.business.resp.ConfirmOrderQueryResp;
import com.jxau.train.common.resp.PageResp;

import java.util.List;

public interface AfterConfirmOrderService {


    //修改座位售卖信息
    void afterDoConfirm(DailyTrainTicket dailyTrainTicket,
                        List<DailyTrainSeat> finalSeatList,
                        List<ConfirmOrderTicketReq> tickets,
                        ConfirmOrder confirmOrder) throws Exception;

}
