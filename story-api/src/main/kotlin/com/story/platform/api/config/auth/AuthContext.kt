package com.story.platform.api.config.auth

import com.story.platform.core.common.enums.ServiceType

data class AuthContext(
    val serviceType: ServiceType,
    val requestId: String,
)
