package com.jxau.train.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.jxau.train.common.exception.BusinessException;
import com.jxau.train.common.exception.BusinessExceptionEnum;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.member.domain.Member;
import com.jxau.train.member.domain.MemberExample;
import com.jxau.train.member.mapper.MemberMapper;
import com.jxau.train.member.req.MemberRegisterReq;
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
    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        //增加判断数据库是否已经存在电话号码
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if(CollUtil.isNotEmpty(list)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        Member member = new Member();
//        member.setId(System.currentTimeMillis());
//        member.setId(1L);
        member.setId(SnowUtil.getSnowFlakeId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
