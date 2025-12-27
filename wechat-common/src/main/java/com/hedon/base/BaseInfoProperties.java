package com.hedon.base;

import com.hedon.utils.RedisOperator;

import jakarta.annotation.Resource;

public class BaseInfoProperties {

    @Resource
    public RedisOperator redis;
}
