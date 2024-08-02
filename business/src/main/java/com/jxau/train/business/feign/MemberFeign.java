package com.jxau.train.business.feign;

import com.jxau.train.common.req.MemberTicketReq;
import com.jxau.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member",url = "http://localhost:8001")
public interface MemberFeign {



    @PostMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req);
}
