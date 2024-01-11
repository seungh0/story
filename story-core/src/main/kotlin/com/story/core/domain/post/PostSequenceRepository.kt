package com.story.core.domain.post

import com.story.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostSequenceRepository(
    private val postSequenceRepository: StringRedisRepository<PostSequenceKey, Long>,
) {

    suspend fun generate(postSpaceKey: PostSpaceKey): Long {
        return postSequenceRepository.incr(
            key = PostSequenceKey(postSpaceKey = postSpaceKey)
        )
    }

    suspend fun set(postSpaceKey: PostSpaceKey, count: Long) {
        postSequenceRepository.set(
            key = PostSequenceKey(postSpaceKey = postSpaceKey),
            value = count,
        )
    }

    suspend fun del(postSpaceKey: PostSpaceKey) {
        postSequenceRepository.del(key = PostSequenceKey(postSpaceKey = postSpaceKey))
    }

    suspend fun getLastSequence(postSpaceKey: PostSpaceKey): Long {
        return postSequenceRepository.get(
            key = PostSequenceKey(postSpaceKey = postSpaceKey)
        ) ?: START_POST_SEQ
    }

    companion object {
        const val START_POST_SEQ = 1L
    }

}
