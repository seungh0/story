package com.story.platform.core.domain.post

import com.story.platform.core.support.redis.StringRedisKey
import java.time.Duration

data class PostIdGenerateKey(
    val postSpaceKey: PostSpaceKey,
    val accountId: String,
) : StringRedisKey<PostIdGenerateKey, Long> {

    override fun getKey(): String =
        "post:st:${postSpaceKey.serviceType}:account:$accountId:st:${postSpaceKey.spaceType}:si:${postSpaceKey.spaceId}"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
