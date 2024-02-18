package com.story.core.domain.post

import com.story.core.domain.post.section.PostSection
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostCreator(
    private val postSequenceRepository: PostSequenceRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        parentId: PostKey?,
        ownerId: String,
        title: String,
        sections: List<PostSectionContentRequest>,
    ): PostResponse {
        if (parentId != null) {
            val parentPost = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId.parentId ?: StringUtils.EMPTY,
                slotId = PostSlotAssigner.assign(postId = parentId.postId),
                postId = parentId.postId,
            ) ?: throw ParentPostNotExistsException("부모 포스트($parentId)가 존재하지 않습니다")

            if (!parentPost.getMetadata<Boolean>(type = PostMetadataType.HAS_CHILDREN)) {
                parentPost.metadata[PostMetadataType.HAS_CHILDREN] = true.toString()
                postRepository.save(parentPost)
            }
        }

        val postId = postSequenceRepository.generate(postSpaceKey = postSpaceKey, parentId = parentId)
        val post = Post.of(
            postSpaceKey = postSpaceKey,
            ownerId = ownerId,
            parentId = parentId,
            postId = postId,
            title = title,
        )

        val postSections = sections.map { section ->
            PostSection.of(
                postSpaceKey = postSpaceKey,
                parentId = parentId,
                postId = postId,
                content = section.toSection(),
                sectionType = section.sectionType(),
                priority = section.priority,
            )
        }

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .upsert(postSections)
            .executeCoroutine()

        return PostResponse.of(post = post, sections = postSections)
    }

}
