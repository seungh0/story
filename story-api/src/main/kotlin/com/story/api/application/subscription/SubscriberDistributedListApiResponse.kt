package com.story.api.application.subscription

import com.story.core.common.model.dto.CursorResponse

data class SubscriberDistributedListApiResponse<T>(
    val data: T,
    val cursor: CursorResponse<String>,
)
