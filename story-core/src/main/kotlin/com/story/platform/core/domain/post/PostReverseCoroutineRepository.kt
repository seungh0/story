package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostReverseCoroutineRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyServiceTypeAndKeyAccountIdAndKeyPostIdAndKeySpaceTypeAndKeySpaceId(
        serviceType: ServiceType,
        accountId: String,
        postId: Long,
        spaceType: PostSpaceType,
        spaceId: String,
    ): PostReverse?

}
