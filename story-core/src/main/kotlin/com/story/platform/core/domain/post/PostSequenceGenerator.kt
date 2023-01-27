package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class PostSequenceGenerator(
    private val postSequenceRepository: StringRedisRepository<PostSequenceKey, Long>,
) {

    suspend fun generate(
        serviceType: ServiceType,
        accountId: String,
        spaceType: String,
        spaceId: String,
    ) = postSequenceRepository.incr(
        key = PostSequenceKey(
            serviceType = serviceType,
            accountId = accountId,
            spaceType = spaceType,
            spaceId = spaceId,
        )
    )

    suspend fun getLastSequence(
        serviceType: ServiceType,
        accountId: String,
        spaceType: String,
        spaceId: String,
    ) = postSequenceRepository.get(
        key = PostSequenceKey(
            serviceType = serviceType,
            accountId = accountId,
            spaceType = spaceType,
            spaceId = spaceId,
        )
    ) ?: INIT_POST_SEQUENCE

    companion object {
        const val INIT_POST_SEQUENCE = 0L
    }

}
