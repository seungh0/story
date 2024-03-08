package com.story.api.application.post

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.post.PostMetadataResponse
import com.story.core.domain.post.PostResponse

data class PostApiResponse(
    val parentId: String?,
    val postId: String,
    val depth: Int,
    val title: String,
    val sections: List<PostSectionApiResponse>,
    val extra: Map<String, String>,
    val owner: PostOwnerApiResponse,
    val metadata: PostMetadataApiResponse?,
) : FeedPayload, AuditingTimeResponse() {

    companion object {
        fun of(post: PostResponse, requestUserId: String?): PostApiResponse {
            val response = PostApiResponse(
                parentId = post.parentId?.serialize(),
                postId = post.postId.serialize(),
                depth = post.depth,
                title = post.title,
                sections = post.sections.map { section ->
                    PostSectionApiResponse(
                        sectionType = section.sectionType(),
                        data = section,
                    )
                },
                owner = PostOwnerApiResponse.of(
                    ownerId = post.ownerId,
                    requestUserId = requestUserId,
                ),
                metadata = post.metadata?.let { PostMetadataApiResponse.of(metadata = it) },
                extra = post.extra,
            )
            response.setAuditingTime(post)
            return response
        }
    }

}

data class PostMetadataApiResponse(
    val hasChildren: Boolean,
) {

    companion object {
        fun of(metadata: PostMetadataResponse) = PostMetadataApiResponse(
            hasChildren = metadata.hasChildren,
        )
    }

}
