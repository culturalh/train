package com.jxau.train.member.service;


import com.jxau.train.member.req.MemberLoginReq;
import com.jxau.train.member.req.MemberRegisterReq;
import com.jxau.train.member.req.MemberSendCodeReq;
import com.jxau.train.member.resp.MemberLoginResp;

public interface MemberService {

    int count();


    //注册
    long register(MemberRegisterReq req);

    //发送短信验证码,没有对接真正的短信接口，测试使用
    void sendCode(MemberSendCodeReq req);

    //登录
    MemberLoginResp login(MemberLoginReq req);
}
