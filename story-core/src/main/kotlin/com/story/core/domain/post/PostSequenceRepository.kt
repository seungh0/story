package com.story.core.domain.post

import com.story.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostSequenceRepository(
    private val postSequenceRepository: StringRedisRepository<PostSequenceKey, Long>,
) {

    suspend fun generatePostNo(postSpaceKey: PostSpaceKey, parentId: PostId?): Long {
        return postSequenceRepository.incr(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId)
        )
    }

    suspend fun set(postSpaceKey: PostSpaceKey, parentId: PostId?, value: Long) {
        postSequenceRepository.set(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId),
            value = value,
        )
    }

    suspend fun del(postSpaceKey: PostSpaceKey, parentId: PostId?) {
        postSequenceRepository.del(key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId))
    }

    suspend fun getLastSequence(postSpaceKey: PostSpaceKey, parentId: PostId?): Long {
        return postSequenceRepository.get(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId)
        ) ?: START_POST_SEQ
    }

    companion object {
        const val START_POST_SEQ = 1L
    }

}
