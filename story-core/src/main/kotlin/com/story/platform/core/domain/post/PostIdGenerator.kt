package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostIdGenerator(
    private val postIdRepository: StringRedisRepository<PostIdGeneratorKey, Long>,
) {

    suspend fun generate(postSpaceKey: PostSpaceKey): String {
        val postId = postIdRepository.incr(
            key = PostIdGeneratorKey(postSpaceKey = postSpaceKey)
        )
        return postId.toString()
    }

    suspend fun getLastPostId(postSpaceKey: PostSpaceKey): String {
        val postId = postIdRepository.get(
            key = PostIdGeneratorKey(postSpaceKey = postSpaceKey)
        ) ?: INIT_POST_ID
        return postId.toString()
    }

    companion object {
        const val INIT_POST_ID = 1L
    }

}
