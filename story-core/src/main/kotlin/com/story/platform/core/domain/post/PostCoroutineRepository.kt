package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostCoroutineRepository : CoroutineCrudRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        serviceType: ServiceType,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Post?

}
