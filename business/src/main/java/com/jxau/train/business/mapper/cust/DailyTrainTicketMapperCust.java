package com.jxau.train.business.mapper.cust;

import com.jxau.train.business.domain.DailyTrainTicket;
import com.jxau.train.business.domain.DailyTrainTicketExample;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface DailyTrainTicketMapperCust {
    void updateCountBySell(Date date, String trainCode,String seatTypeCode,
        Integer minStartIndex,
        Integer maxStartIndex,
        Integer minEndIndex,
        Integer maxEndIndex
        );
}