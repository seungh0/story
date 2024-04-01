package com.story.core.common.model.dto

import com.story.core.common.distribution.SlotRangeMarker

data class SlotRangeMarkerResponse<T>(
    val data: T,
    val nextMarker: SlotRangeMarker?,
)
