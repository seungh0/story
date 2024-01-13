package com.story.api.application.feed.mapping

import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.time.DurationMax
import org.hibernate.validator.constraints.time.DurationMin
import java.time.Duration

data class FeedMappingCreateApiRequest(
    @field:Size(max = 300)
    val description: String = "",

    @field:DurationMin(hours = 1)
    @field:DurationMax(days = 100)
    val retention: Duration,
)
