package com.story.api.application.post

import com.story.core.domain.post.PostMetadataResponse

data class PostMetadataResponse(
    val hasChildren: Boolean,
) {

    companion object {
        fun of(metadata: PostMetadataResponse) = com.story.api.application.post.PostMetadataResponse(
            hasChildren = metadata.hasChildren,
        )
    }

}
