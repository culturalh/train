package com.jxau.train.batch.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * springboot自带的定时任务只适合单体应用，不适合集群应用
 * 没法实时更改任务状态和策略
 */
//@Component
//@EnableScheduling
public class SpringBootTestJob {


    @Scheduled(cron = "0/5 * * * * ?")
    public void test() {
        System.out.println("test springboot job");
    }
}
