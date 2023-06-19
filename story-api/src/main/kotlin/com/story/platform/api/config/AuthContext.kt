package com.story.platform.api.config

import com.story.platform.core.common.enums.ServiceType

data class AuthContext(
    val serviceType: ServiceType,
)
