package com.jxau.train.member.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.jxau")

public class MemberApplication {
    //创建日志对象
    private static final Logger LOG = LoggerFactory.getLogger(MemberApplication.class);
    public static void main(String[] args) {
//        SpringApplication.run(MemberApplication.class, args);

        SpringApplication app = new SpringApplication(MemberApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("Member地址: \thttp://127.0.0.1{}/member",env.getProperty("server.port"));
    }
}
