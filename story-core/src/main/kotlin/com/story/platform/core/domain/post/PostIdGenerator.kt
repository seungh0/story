package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostIdGenerator(
    private val postIdRepository: StringRedisRepository<PostIdGenerateKey, Long>,
) {

    suspend fun generate(postSpaceKey: PostSpaceKey) = postIdRepository.incr(
        key = PostIdGenerateKey(postSpaceKey = postSpaceKey)
    )

    suspend fun getLastPostId(postSpaceKey: PostSpaceKey) = postIdRepository.get(
        key = PostIdGenerateKey(postSpaceKey = postSpaceKey)
    ) ?: INIT_POST_ID

    companion object {
        const val INIT_POST_ID = 1L
    }

}
