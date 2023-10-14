package com.story.platform.api.domain.resource

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ResourceListApiRequest(
    @field:Min(value = 1)
    @field:Max(value = 50)
    val pageSize: Int = 0,
)
