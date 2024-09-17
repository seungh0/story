package com.story.api.application.feed

import org.hibernate.validator.constraints.time.DurationMax
import org.hibernate.validator.constraints.time.DurationMin
import java.time.Duration

data class FeedItemOptionsCreateRequest(
    @field:DurationMin(hours = 1)
    @field:DurationMax(days = 100)
    val retention: Duration,
)
