package com.story.data.redis

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan(basePackageClasses = [RedisDataRoot::class])
@Configuration
class RedisDataRoot
