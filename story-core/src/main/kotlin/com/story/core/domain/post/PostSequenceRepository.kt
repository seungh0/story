package com.story.core.domain.post

import com.story.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostSequenceRepository(
    private val postSequenceRepository: StringRedisRepository<PostSequenceKey, Long>,
) {

    suspend fun generate(postSpaceKey: PostSpaceKey, parentId: PostKey?): Long {
        return postSequenceRepository.incr(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId)
        )
    }

    suspend fun set(postSpaceKey: PostSpaceKey, parentId: PostKey?, count: Long) {
        postSequenceRepository.set(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId),
            value = count,
        )
    }

    suspend fun del(postSpaceKey: PostSpaceKey, parentId: PostKey?) {
        postSequenceRepository.del(key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId))
    }

    suspend fun getLastSequence(postSpaceKey: PostSpaceKey, parentId: PostKey?): Long {
        return postSequenceRepository.get(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId)
        ) ?: START_POST_SEQ
    }

    companion object {
        const val START_POST_SEQ = 1L
    }

}
