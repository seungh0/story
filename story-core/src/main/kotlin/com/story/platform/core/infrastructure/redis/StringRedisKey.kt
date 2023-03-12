package com.story.platform.core.infrastructure.redis

import java.time.Duration

interface StringRedisKey<K : StringRedisKey<K, V>, V> {

    fun makeKeyString(): String

    fun serializeValue(value: V): String

    fun deserializeValue(value: String?): V?

    fun getTtl(): Duration?

}
