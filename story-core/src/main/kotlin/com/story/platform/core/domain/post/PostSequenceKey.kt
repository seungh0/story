package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class PostSequenceKey(
    val postSpaceKey: PostSpaceKey,
) : StringRedisKey<PostSequenceKey, Long> {

    override fun makeKeyString(): String =
        "post-sequence:service:${postSpaceKey.serviceType}:space:${postSpaceKey.spaceType}:${postSpaceKey.spaceId}"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
