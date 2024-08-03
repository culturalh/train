package com.jxau.train.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.jxau")

public class GatewayApplication {
    //创建日志对象
    private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);
    public static void main(String[] args) {
//        SpringApplication.run(MemberApplication.class, args);

        SpringApplication app = new SpringApplication(GatewayApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        //需要加上测试地址,比如http://127.0.0.1:8080/member/hello
        LOG.info("Gateway地址: \thttp://127.0.0.1:{}/",env.getProperty("server.port"));
    }
}
