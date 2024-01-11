package com.story.api.application.feed

import jakarta.validation.constraints.Size

data class FeedMappingCreateApiRequest(
    @field:Size(max = 300)
    val description: String = "",
)
