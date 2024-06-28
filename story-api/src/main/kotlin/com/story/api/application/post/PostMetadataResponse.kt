package com.story.api.application.post

import com.story.core.domain.post.PostMetadata

data class PostMetadataResponse(
    val hasChildren: Boolean,
) {

    companion object {
        fun of(metadata: PostMetadata) = com.story.api.application.post.PostMetadataResponse(
            hasChildren = metadata.hasChildren,
        )
    }

}
