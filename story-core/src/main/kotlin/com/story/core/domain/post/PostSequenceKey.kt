package com.story.core.domain.post

import com.story.core.infrastructure.redis.StringRedisKey
import java.time.Duration

data class PostSequenceKey(
    val postSpaceKey: PostSpaceKey,
    val parentId: PostKey?,
) : StringRedisKey<PostSequenceKey, Long> {

    override fun makeKeyString(): String {
        if (parentId == null) {
            return "post-sequence:v1:${postSpaceKey.workspaceId}:${postSpaceKey.spaceId}"
        }
        return "post-sequence:v1:${postSpaceKey.workspaceId}:${postSpaceKey.spaceId}:${parentId.serialize()}"
    }

    override fun deserializeValue(value: String?): Long? = value?.toLongOrNull()

    override fun getTtl(): Duration? = null

    override fun serializeValue(value: Long): String = value.toString()

}
