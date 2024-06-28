package com.story.core.domain.post

import com.story.core.common.distribution.DistributionKey
import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.post.section.PostSectionContentCommand

interface PostRepository {

    suspend fun putMetadata(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: PostId?,
        postId: PostId,
        metadataType: PostMetadataType,
        value: Any,
    ): Boolean

    suspend fun create(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        postNo: Long,
        ownerId: String,
        title: String,
        sections: List<PostSectionContentCommand>,
        extra: Map<String, String>,
    ): PostWithSections

    suspend fun modify(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        postNo: Long,
        ownerId: String,
        title: String?,
        sections: List<PostSectionContentCommand>?,
        extra: Map<String, String>?,
    ): PostPatchResponse

    suspend fun delete(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostId,
    )

    suspend fun findPost(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
    ): Post?

    suspend fun findPostWithSections(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
    ): PostWithSections?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        cursorRequest: CursorRequest,
        sortBy: PostSortBy,
    ): Slice<PostWithSections, String>

    suspend fun listOwnerPosts(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        cursorRequest: CursorRequest,
    ): Slice<PostWithSections, String>

    suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long

}
