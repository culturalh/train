package com.jxau.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxau.train.business.enums.RedisKeyPreEnum;
import com.jxau.train.business.mapper.cust.SkTokenMapperCust;
import com.jxau.train.business.service.DailyTrainSeatService;
import com.jxau.train.business.service.DailyTrainStationService;
import com.jxau.train.common.resp.PageResp;
import com.jxau.train.common.util.SnowUtil;
import com.jxau.train.business.domain.SkToken;
import com.jxau.train.business.domain.SkTokenExample;
import com.jxau.train.business.mapper.SkTokenMapper;
import com.jxau.train.business.req.SkTokenQueryReq;
import com.jxau.train.business.req.SkTokenSaveReq;
import com.jxau.train.business.resp.SkTokenQueryResp;
import com.jxau.train.business.service.SkTokenService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenServiceImpl implements SkTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenServiceImpl.class);

    @Resource
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private SkTokenMapperCust skTokenMapperCust;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void save(SkTokenSaveReq req) {
        Date now = new Date();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if(ObjectUtil.isNull(req.getId())){
            //使用雪花算法生成ID
            skToken.setId(SnowUtil.getSnowFlakeId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        }else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    @Override
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {

        //mybatis条件查询类
        SkTokenExample skTokenExample = new SkTokenExample();
        //创建条件
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();


        //开始分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);
        //分页结果
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<SkTokenQueryResp> skTokenQueryResp = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);

        //封装分页结果
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(skTokenQueryResp);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    @Override
    public boolean vaildSkToken(Date date, String trainCode, Long memberId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始",memberId, DateUtil.formatDate(date), trainCode);

        //先校验令牌，在校验令牌余量，防止机器人抢票
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        Boolean isTrue = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "value", 5, TimeUnit.SECONDS);
        if(Boolean.TRUE.equals(isTrue)){
            LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌成功",memberId, DateUtil.formatDate(date), trainCode);
        }else {
            LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌失败",memberId, DateUtil.formatDate(date), trainCode);
            return false;
        }


        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" +DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = stringRedisTemplate.opsForValue().get(skTokenCountKey);
        if(skTokenCount != null){
            LOG.info("缓存中有改车次的令牌大闸key:{}",skTokenCountKey);
            Long count = stringRedisTemplate.opsForValue().decrement(skTokenCountKey, 1);

            if(count < 0L){
                LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌失败",memberId, DateUtil.formatDate(date), trainCode);
                return false;
            }else {
                LOG.info("令牌余数：{}",count);
                stringRedisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
                //每五次扣减一次数据库
                if (count % 5 == 0){
                    skTokenMapperCust.decrease(date, trainCode,5);
                }
                return true;
            }
        }else {
            LOG.info("缓存中没有改车次的令牌大闸key:{}",skTokenCountKey);
            //检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(tokenCountList)){
                LOG.info("找不到日期【{}】车次【{}】的令牌",memberId, DateUtil.formatDate(date), trainCode);
                return false;
            }
            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount() <= 0){
                LOG.info("日期【{}】车次【{}】的令牌数为0", DateUtil.formatDate(date), trainCode);
                return false;
            }
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            LOG.info("将该日期改车次大闸放入缓存中：key:{},count:{}",skTokenCountKey,count);
            stringRedisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
            skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }


        //令牌约等于库存，令牌卖完，就先不卖票
//        int decrease = skTokenMapperCust.decrease(date, trainCode,decreaseCount);
//        if(decrease > 0){
//            return true;
//        }else {
//            return false;
//        }
    }




//    @Override
//    public boolean vaildSkToken(Date date, String trainCode, Long memberId) {
//        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);
//
//        // 需要去掉这段，否则发布生产后，体验多人排队功能时，会因拿不到锁而返回：等待5秒，加入20人时，只有第1次循环能拿到锁
//        // if (!env.equals("dev")) {
//        //     // 先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌，用来表示【谁能做什么】的一个凭证
//        //     String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
//        //     Boolean setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
//        //     if (Boolean.TRUE.equals(setIfAbsent)) {
//        //         LOG.info("恭喜，抢到令牌锁了！lockKey：{}", lockKey);
//        //     } else {
//        //         LOG.info("很遗憾，没抢到令牌锁！lockKey：{}", lockKey);
//        //         return false;
//        //     }
//        // }
//
//        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
//        Object skTokenCount = stringRedisTemplate.opsForValue().get(skTokenCountKey);
//        if (skTokenCount != null) {
//            LOG.info("缓存中有该车次令牌大闸的key：{}", skTokenCountKey);
//            Long count = stringRedisTemplate.opsForValue().decrement(skTokenCountKey, 1);
//            if (count < 0L) {
//                LOG.error("获取令牌失败：{}", skTokenCountKey);
//                return false;
//            } else {
//                LOG.info("获取令牌后，令牌余数：{}", count);
//                stringRedisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
//                // 每获取5个令牌更新一次数据库
//                if (count % 5 == 0) {
//                    skTokenMapperCust.decrease(date, trainCode, 5);
//                }
//                return true;
//            }
//        } else {
//            LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
//            // 检查是否还有令牌
//            SkTokenExample skTokenExample = new SkTokenExample();
//            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
//            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
//            if (CollUtil.isEmpty(tokenCountList)) {
//                LOG.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
//                return false;
//            }
//
//            SkToken skToken = tokenCountList.get(0);
//            if (skToken.getCount() <= 0) {
//                LOG.info("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
//                return false;
//            }
//
//            // 令牌还有余量
//            // 令牌余数-1
//            Integer count = skToken.getCount() - 1;
//            skToken.setCount(count);
//            LOG.info("将该车次令牌大闸放入缓存中，key: {}， count: {}", skTokenCountKey, count);
//            // 不需要更新数据库，只要放缓存即可
//            stringRedisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
//            skTokenMapper.updateByPrimaryKey(skToken);
//            return true;
//        }
//
//        // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
//        // int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
//        // if (updateCount > 0) {
//        //     return true;
//        // } else {
//        //     return false;
//        // }
//    }


    public void genDaily(Date date,String trainCode) {
        LOG.info("删除日期【{}】车次【{}】的记录", DateUtil.formatDate(date), trainCode);

        SkTokenExample skTokenExample = new SkTokenExample();
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();
        criteria.andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = new DateTime();
        SkToken skToken = new SkToken();
        skToken.setId(SnowUtil.getSnowFlakeId());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        //总座位数
        int countSeat = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("日期【{}】车次【{}】的座位【{}】记录", DateUtil.formatDate(date), trainCode, countSeat);
        //总车站
        long stationCount = dailyTrainStationService.countByTrainCode(date,trainCode);
        LOG.info("日期【{}】车次【{}】的车站【{}】记录", DateUtil.formatDate(date), trainCode, stationCount);

        //预估令牌数量
        int count = (int) (countSeat * stationCount * 3/4);
        skToken.setCount(count);
        skTokenMapper.insert(skToken);


    }
}
