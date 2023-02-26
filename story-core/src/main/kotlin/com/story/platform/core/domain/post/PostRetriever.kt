package com.story.platform.core.domain.post

import com.story.platform.core.common.error.NotFoundException
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postReactiveRepository: PostReactiveRepository,
    private val postCoroutineRepository: PostCoroutineRepository,
    private val postReverseCoroutineRepository: PostReverseCoroutineRepository,
) {

    suspend fun findPost(
        spaceKey: PostSpaceKey,
        postId: Long,
    ): Post {
        return postCoroutineRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = spaceKey.serviceType,
            spaceType = spaceKey.spaceType,
            spaceId = spaceKey.spaceId,
            slotId = PostSlotAllocator.allocate(postId),
            postId = postId,
        ) ?: throw NotFoundException("해당하는 Space($spaceKey)에 포스트($postId)가 존재하지 않습니다")
    }

}
