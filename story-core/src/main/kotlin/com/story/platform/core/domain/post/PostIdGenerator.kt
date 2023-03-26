package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostIdGenerator(
    private val postIdRepository: StringRedisRepository<PostIdGeneratorKey, Long>,
) {

    suspend fun generate(postSpaceKey: PostSpaceKey) = postIdRepository.incr(
        key = PostIdGeneratorKey(postSpaceKey = postSpaceKey)
    )

    suspend fun getLastPostId(postSpaceKey: PostSpaceKey) = postIdRepository.get(
        key = PostIdGeneratorKey(postSpaceKey = postSpaceKey)
    ) ?: INIT_POST_ID

    companion object {
        const val INIT_POST_ID = 1L
    }

}
