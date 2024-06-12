package com.story.data.redis.post

import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostSpaceKey
import com.story.core.support.redis.StringRedisKey
import java.time.Duration

data class PostSequenceKey(
    val postSpaceKey: PostSpaceKey,
    val parentId: PostId?,
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
