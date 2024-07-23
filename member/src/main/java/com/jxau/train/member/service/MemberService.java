package com.jxau.train.member.service;


import com.jxau.train.member.req.MemberRegisterReq;

public interface MemberService {

    int count();


    //注册
    long register(MemberRegisterReq req);

}
