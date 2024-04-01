package com.story.api.application.subscription

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SubscriberDistributedMarkerListRequest(
    @field:Min(value = 1)
    @field:Max(value = 100)
    val markerSize: Int = 0,
)
