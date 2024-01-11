package com.story.core.domain.post.section

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostSectionRepository : CoroutineCrudRepository<PostSection, PostSectionPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Flow<PostSection>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdIn(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postIds: Collection<Long>,
    ): Flow<PostSection>

}
