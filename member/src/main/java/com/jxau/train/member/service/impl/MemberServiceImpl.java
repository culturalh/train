package com.jxau.train.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.jxau.train.member.domain.Member;
import com.jxau.train.member.domain.MemberExample;
import com.jxau.train.member.mapper.MemberMapper;
import com.jxau.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Resource
    private MemberMapper memberMapper;
    @Override
    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    @Override
    public long register(String mobile) {

        //增加判断数据库是否已经存在电话号码
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if(CollUtil.isNotEmpty(list)){
            throw new RuntimeException("该手机号已经注册过了");
        }
        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
