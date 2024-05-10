package com.story.core.domain.post

data class PostMetadataResponse(
    val hasChildren: Boolean,
) {

    companion object {
        fun of(post: PostEntity) = PostMetadataResponse(
            hasChildren = post.getMetadata(type = PostMetadataType.HAS_CHILDREN),
        )
    }

}
