package com.story.platform.core.domain.reaction

import com.story.platform.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class ReactionCountKey(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
    val optionId: String,
) : StringRedisKey<ReactionCountKey, Long> {

    override fun makeKeyString(): String =
        "reactions-count:v1:$workspaceId:$componentId:$targetId:$optionId"

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
