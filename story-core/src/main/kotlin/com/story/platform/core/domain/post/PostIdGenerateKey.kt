package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class PostIdGenerateKey(
    val postSpaceKey: PostSpaceKey,
) : StringRedisKey<PostIdGenerateKey, Long> {

    override fun makeKeyString(): String =
        "post:serviceType:${postSpaceKey.serviceType}:spaceType:${postSpaceKey.spaceType}:spaceId:${postSpaceKey.spaceId}"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
