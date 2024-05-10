package com.story.api.application.post

import com.story.core.domain.post.PostMetadataResponse

data class PostMetadataApiResponse(
    val hasChildren: Boolean,
) {

    companion object {
        fun of(metadata: PostMetadataResponse) = PostMetadataApiResponse(
            hasChildren = metadata.hasChildren,
        )
    }

}
