package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.redis.StringRedisKey
import java.time.Duration

data class PostSequenceKey(
    val serviceType: ServiceType,
    val accountId: String,
    val spaceType: String,
    val spaceId: String,
) : StringRedisKey<PostSequenceKey, Long> {

    override fun getKey(): String = "post:st:$serviceType:account:$accountId:st:$spaceType:si:$spaceId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
