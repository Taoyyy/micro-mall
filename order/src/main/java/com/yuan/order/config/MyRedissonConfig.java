package com.yuan.order.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@Configuration
@ComponentScan
@EnableCaching
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.231.136:6379");
        return Redisson.create(config);
    }
}
