package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository : CoroutineCrudRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        serviceType: ServiceType,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Post?

    suspend fun findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
        serviceType: ServiceType,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
        serviceType: ServiceType,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
        serviceType: ServiceType,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
        serviceType: ServiceType,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

}
