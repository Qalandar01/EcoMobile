package com.example.ecomobile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {


    private static final long EXPIRE_TIME = 5;
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
    }

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, EXPIRE_TIME, TimeUnit.MINUTES);
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(email);
    }
}
