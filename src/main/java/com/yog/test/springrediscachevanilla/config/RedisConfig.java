package com.yog.test.springrediscachevanilla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories // enable redis repositories mandatory for redis cache
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        //host and port config when using JedisConnectionFactory on prod environment
        //RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<Object, Object> template() {
        // redisTemplate bean is mandatory for redis cache
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
