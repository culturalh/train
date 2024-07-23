package com.jxau.train.common.util;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {

    private static long dataCenterId = 1;//数据中心
    private static long workerId = 1;//机器标识

    public static long getSnowFlakeId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
    }
    public static String  getSnowFlakeIdStr() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }
}
