package com.nowcoder.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan({"com.nowcoder.community.dao"})
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

}
