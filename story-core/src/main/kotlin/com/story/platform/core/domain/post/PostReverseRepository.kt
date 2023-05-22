package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostReverseRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyServiceTypeAndKeyAccountIdAndKeyPostIdAndKeySpaceTypeAndKeySpaceId(
        serviceType: ServiceType,
        accountId: String,
        postId: String,
        spaceType: PostSpaceType,
        spaceId: String,
    ): PostReverse?

}
