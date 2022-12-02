package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;


@Configuration
public class AlphaConfig {
    @Bean
    public SimpleDateFormat simpleDateFormatFormat() {
        return new SimpleDateFormat("yyyy-mm-dd HH-mm-ss");
    }
}
