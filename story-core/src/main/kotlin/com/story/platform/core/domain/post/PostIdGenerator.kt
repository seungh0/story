package com.story.platform.core.domain.post

import com.story.platform.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostIdGenerator(
    private val postIdRepository: StringRedisRepository<PostIdGenerateKey, Long>,
) {

    suspend fun generate(
        postSpaceKey: PostSpaceKey,
        accountId: String,
    ) = postIdRepository.incr(
        key = PostIdGenerateKey(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
        )
    )

    suspend fun getLastPostId(
        postSpaceKey: PostSpaceKey,
        accountId: String,
    ) = postIdRepository.get(
        key = PostIdGenerateKey(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
        )
    ) ?: INIT_POST_ID

    companion object {
        const val INIT_POST_ID = 0L
    }

}
