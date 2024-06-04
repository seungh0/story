package com.story.api.application.post

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.post.PostWithSections

data class PostResponse(
    val parentId: String?,
    val postId: String,
    val depth: Int,
    val title: String,
    val sections: List<PostSectionResponse>,
    val extra: Map<String, String>,
    val owner: PostOwnerResponse,
    val metadata: PostMetadataResponse?,
) : FeedPayload, AuditingTimeResponse() {

    companion object {
        fun of(post: PostWithSections, requestUserId: String?): PostResponse {
            val response = PostResponse(
                parentId = post.parentId?.serialize(),
                postId = post.postId.serialize(),
                depth = post.depth,
                title = post.title,
                sections = post.sections.map { section ->
                    PostSectionResponse(
                        sectionType = section.sectionType(),
                        data = section,
                    )
                },
                owner = PostOwnerResponse.of(
                    ownerId = post.ownerId,
                    requestUserId = requestUserId,
                ),
                metadata = post.metadata?.let { PostMetadataResponse.of(metadata = it) },
                extra = post.extra,
            )
            response.setAuditingTime(post)
            return response
        }
    }

}
