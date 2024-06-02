package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId

data class Feed(
    val feedId: Long,
    val subscriberId: String,
    val eventKey: String,
    val sourceResourceId: ResourceId,
    val sourceComponentId: String,
)
