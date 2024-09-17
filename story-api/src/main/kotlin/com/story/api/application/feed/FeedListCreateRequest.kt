package com.story.api.application.feed

import jakarta.validation.Valid
import jakarta.validation.constraints.Size

data class FeedListCreateRequest(
    @field:Valid
    @field:Size(min = 1, max = 100)
    val feeds: List<FeedCreateRequest>,
    val options: FeedItemOptionsCreateRequest,
)
