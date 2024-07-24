package com.jxau.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.jxau.train.common.exception.BusinessException;
import com.jxau.train.common.exception.BusinessExceptionEnum;
import com.jxau.train.common.util.JwtUtil;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.member.domain.Member;
import com.jxau.train.member.domain.MemberExample;
import com.jxau.train.member.mapper.MemberMapper;
import com.jxau.train.member.req.MemberLoginReq;
import com.jxau.train.member.req.MemberRegisterReq;
import com.jxau.train.member.req.MemberSendCodeReq;
import com.jxau.train.member.resp.MemberLoginResp;
import com.jxau.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

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
        Member memberDB= selectByMobile(mobile);
        if(ObjectUtil.isNotNull(memberDB)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        Member member = new Member();
        member.setId(SnowUtil.getSnowFlakeId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    @Override
    public void sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();
        //增加判断数据库是否已经存在电话号码
        Member memberDB= selectByMobile(mobile);
        if(ObjectUtil.isNull(memberDB)){
            LOG.info("手机号不存在，进行注册,插入一条记录");
            //注册
            Member member = new Member();
            member.setId(SnowUtil.getSnowFlakeId());
            member.setMobile(mobile);
            memberMapper.insert(member);
//            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }else {
            LOG.info("手机号存在，无需插入一条记录");
        }
        //生成验证码
        String code = RandomUtil.randomString(4);
        LOG.info("短信验证码：{}",code);
        // TODO2024/7/23 短信记录表：手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        //TODO对接短信通道，发送短信
    }

    @Override
    public MemberLoginResp login(MemberLoginReq req) {
        String mobile = req.getMobile();
        //增加判断数据库是否已经存在电话号码
        Member memberDB= selectByMobile(mobile);
        if(ObjectUtil.isNull(memberDB)){
//            LOG.info("手机号不存在，进行注册,插入一条记录");
          throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }else {
            LOG.info("手机号存在，无需插入一条记录");
        }
        //生成验证码
//        String code = RandomUtil.randomString(4);
        //测试
        String code = "8888";
        if(!"8888".equals(req.getCode())){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        LOG.info("短信验证码：{}",code);
        MemberLoginResp memberLoginResp = new MemberLoginResp();
        BeanUtil.copyProperties(memberDB,memberLoginResp);

        //JwtUtil生成token
        String token = JwtUtil.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
        memberLoginResp.setToken(token);
        return memberLoginResp;
    }

    //根据mobile查询
    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if(CollUtil.isEmpty(list)){
            return null;
        }
        else {
            return list.get(0);
        }
    }
}
