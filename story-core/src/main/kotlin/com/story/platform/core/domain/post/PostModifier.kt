package com.story.platform.core.domain.post

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.ForbiddenException
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostModifier(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
) {

    suspend fun patch(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String?,
        content: String?,
        extraJson: String?,
    ): PostPatchResponse {
        val slotId = PostSlotAssigner.assign(postId)

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = slotId,
            postId = postId,
        ) ?: throw NotFoundException(
            message = "해당하는 포스트($postId)는 존재하지 않습니다 [postSpaceKey: $postSpaceKey]",
            errorCode = ErrorCode.E404_NOT_FOUND_POST,
        )

        if (!post.isOwner(accountId)) {
            throw ForbiddenException("계정($accountId)는 해당하는 포스트($postId)를 수정할 권한이 없습니다 [postSpaceKey: $postSpaceKey]")
        }

        val hasChanged = post.patch(
            title = title,
            content = content,
            extraJson = extraJson,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .executeCoroutine()

        return PostPatchResponse(post = post, hasChanged = hasChanged)
    }

}
