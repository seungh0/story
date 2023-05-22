package com.story.platform.core.domain.feed

import com.story.platform.core.domain.post.PostSpaceType

data class FeedPostKey(
    val spaceType: PostSpaceType,
    val spaceId: String,
    val slotId: Long,
    val postId: String,
)
