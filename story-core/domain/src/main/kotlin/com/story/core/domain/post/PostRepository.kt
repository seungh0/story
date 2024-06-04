package com.story.core.domain.post

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

}
