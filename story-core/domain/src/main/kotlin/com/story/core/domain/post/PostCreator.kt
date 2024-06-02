package com.story.core.domain.post

import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionManager
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
    private val postSectionManager: PostSectionManager,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        ownerId: String,
        title: String,
        sections: List<PostSectionContentCommand>,
        extra: Map<String, String>,
    ): Post {
        if (parentId != null) {
            val parentPost = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId.parentId ?: StringUtils.EMPTY,
                slotId = PostSlotAssigner.assign(postNo = parentId.postNo),
                postNo = parentId.postNo,
            ) ?: throw PostParentNotExistsException("부모 포스트($parentId)가 존재하지 않습니다")

            if (!parentPost.getMetadata<Boolean>(type = PostMetadataType.HAS_CHILDREN)) {
                postRepository.putMetadata(parentPost.key, PostMetadataType.HAS_CHILDREN, true.toString())
            }
        }

        val postNo = postSequenceRepository.generatePostNo(postSpaceKey = postSpaceKey, parentId = parentId)
        val post = PostEntity.of(
            postSpaceKey = postSpaceKey,
            ownerId = ownerId,
            parentId = parentId,
            postNo = postNo,
            title = title,
            extra = extra,
        )

        val postSections = postSectionManager.makePostSections(
            requests = sections,
            postSpaceKey = postSpaceKey,
            postNo = postNo,
            parentId = parentId,
            ownerId = ownerId,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .upsert(postSections)
            .executeCoroutine()

        return Post.of(
            post = post,
            sections = postSectionManager.makePostSectionContentResponse(postSections)
        )
    }

}
