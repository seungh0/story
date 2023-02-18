package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType

data class PostSpaceKey(
    val serviceType: ServiceType,
    val spaceType: PostSpaceType,
    val spaceId: String,
)
