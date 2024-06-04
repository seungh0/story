package com.story.core.domain.post

import com.story.core.domain.post.section.PostSectionContentCommand
import org.springframework.stereotype.Service

@Service
class PostCreator(
    private val postSequenceRepository: PostSequenceRepository,
    private val postRepository: PostRepository,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        ownerId: String,
        title: String,
        sections: List<PostSectionContentCommand>,
        extra: Map<String, String>,
    ): PostWithSections {
        if (parentId != null) {
            postRepository.putMetadata(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                postId = parentId,
                parentId = parentId.parentPostId(),
                metadataType = PostMetadataType.HAS_CHILDREN,
                value = true,
            )
        }

        val postNo = postSequenceRepository.generatePostNo(postSpaceKey = postSpaceKey, parentId = parentId)

        return postRepository.create(
            postNo = postNo,
            postSpaceKey = postSpaceKey,
            parentId = parentId,
            ownerId = ownerId,
            title = title,
            sections = sections,
            extra = extra,
        )
    }

}
