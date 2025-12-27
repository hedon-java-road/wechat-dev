package com.hedon.utils;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class RedisOperator {
    @Resource
    public RedisTemplate<String, String> redisTemplate;

    public long ttl(String key) {
        if (key == null) {
            return 0;
        }
        return redisTemplate.getExpire(key);
    }

    public long incrment(String key, long delta) {
        if (key == null) {
            return 0;
        }
        return redisTemplate.opsForValue().increment(key);
    }

    public void expire(String key, Duration timeout) {
        redisTemplate.expire(key, timeout);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
}
