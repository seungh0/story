package com.story.platform.api.domain.post

import com.story.platform.core.domain.post.PostSortBy

data class PostListApiRequest(
    val sortBy: PostSortBy = PostSortBy.LATEST,
)
