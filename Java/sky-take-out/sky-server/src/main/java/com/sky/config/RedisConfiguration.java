package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redisTemplate配置类，可以不写
 */
@Slf4j
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        log.info("初始化redisTemplate....");
        // 设置key的序列化器StringRedisSerializer,   默认是JdkSerializationRedisSerializer
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 注意：不推荐修改value的序列化器
        // redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 通过redis的工厂创建对象
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
