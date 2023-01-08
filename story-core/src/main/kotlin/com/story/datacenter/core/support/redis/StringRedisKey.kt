package com.story.datacenter.core.support.redis

import java.time.Duration

interface StringRedisKey<K : StringRedisKey<K, V>, V> {

    fun getKey(): String

    fun serializeValue(value: V): String

    fun deserializeValue(value: String?): V?

    fun getTtl(): Duration?

}
