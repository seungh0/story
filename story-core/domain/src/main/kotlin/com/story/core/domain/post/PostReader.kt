package com.story.core.domain.post

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import org.springframework.stereotype.Service

@Service
class PostReader(
    private val postRepository: PostRepository,
) {

    suspend fun getPost(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
    ): PostWithSections {
        return postRepository.findPostWithSections(
            postSpaceKey = postSpaceKey,
            postId = postId,
        ) ?: throw PostNotExistsException(message = "해당하는 Space($postSpaceKey)에 포스트($postId)가 존재하지 않습니다")
    }

    suspend fun listPosts(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        cursorRequest: CursorRequest,
        sortBy: PostSortBy,
    ): Slice<PostWithSections, String> {
        return postRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
            postSpaceKey = postSpaceKey,
            parentId = parentId,
            sortBy = sortBy,
            cursorRequest = cursorRequest,
        )
    }

    suspend fun listOwnerPosts(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        cursorRequest: CursorRequest,
    ): Slice<PostWithSections, String> {
        return postRepository.listOwnerPosts(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = workspaceId,
            cursorRequest = cursorRequest,
        )
    }

}
