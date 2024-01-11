package com.story.api.application.post

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.post.PostMetadataResponse
import com.story.core.domain.post.PostResponse

data class PostApiResponse(
    val postId: String,
    val title: String,
    val sections: List<PostSectionApiResponse>,
    val isOwner: Boolean,
    val writer: PostWriterApiResponse,
    val metadata: PostMetadataApiResponse,
) : AuditingTimeResponse() {

    companion object {
        fun of(post: PostResponse, requestAccountId: String?): PostApiResponse {
            val response = PostApiResponse(
                postId = post.postId.toString(),
                title = post.title,
                sections = post.sections.map { section ->
                    PostSectionApiResponse(
                        sectionType = section.sectionType(),
                        data = section,
                    )
                },
                isOwner = post.accountId == requestAccountId,
                writer = PostWriterApiResponse(
                    accountId = post.accountId,
                ),
                metadata = PostMetadataApiResponse.of(metadata = post.metadata),
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
