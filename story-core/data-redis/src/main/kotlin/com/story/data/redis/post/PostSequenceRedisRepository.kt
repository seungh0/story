package com.story.data.redis.post

import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostSequenceRepository
import com.story.core.domain.post.PostSpaceKey
import com.story.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostSequenceRedisRepository(
    private val postSequenceRepository: StringRedisRepository<PostSequenceKey, Long>,
) : PostSequenceRepository {

    override suspend fun generatePostNo(postSpaceKey: PostSpaceKey, parentId: PostId?): Long {
        return postSequenceRepository.incr(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId)
        )
    }

    override suspend fun set(postSpaceKey: PostSpaceKey, parentId: PostId?, value: Long) {
        postSequenceRepository.set(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId),
            value = value,
        )
    }

    override suspend fun del(postSpaceKey: PostSpaceKey, parentId: PostId?) {
        postSequenceRepository.del(key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId))
    }

    override suspend fun getLastSequence(postSpaceKey: PostSpaceKey, parentId: PostId?): Long {
        return postSequenceRepository.get(
            key = PostSequenceKey(postSpaceKey = postSpaceKey, parentId = parentId)
        ) ?: START_POST_SEQ
    }

    companion object {
        const val START_POST_SEQ = 1L
    }

}
