package com.jxau.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.common.context.LoginMemberContext;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.member.domain.${Domain};
import com.jxau.train.member.domain.${Domain}Example;
import com.jxau.train.member.mapper.${Domain}Mapper;
import com.jxau.train.member.req.${Domain}QueryReq;
import com.jxau.train.member.req.${Domain}SaveReq;
import com.jxau.train.member.resp.${Domain}QueryResp;
import com.jxau.train.member.service.${Domain}Service;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ${Domain}ServiceImpl implements ${Domain}Service {

    private static final Logger LOG = LoggerFactory.getLogger(${Domain}ServiceImpl.class);

    @Resource
    private ${Domain}Mapper ${domain}Mapper;

    @Override
    public void save(${Domain}SaveReq req) {
        Date now = new Date();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            ${domain}.setId(SnowUtil.getSnowFlakeId());
            //从上下文本地变量中获取登录用户会员ID
            ${domain}.setMemberId(LoginMemberContext.getId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        }else {
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }
    }

    @Override
    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req) {

        //mybatis条件查询类
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        //创建条件
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
        if(ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);
        //分页结果
        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<${Domain}QueryResp> ${domain}QueryResp = BeanUtil.copyToList(${domain}List, ${Domain}QueryResp.class);

        //封装分页结果
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(${domain}QueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
