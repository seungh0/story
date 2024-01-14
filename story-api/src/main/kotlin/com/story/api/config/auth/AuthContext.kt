package com.story.api.config.auth

import com.story.core.common.error.MissingRequestUserIdException

data class AuthContext(
    val workspaceId: String,
    val requestId: String,
    val requestUserId: String?,
) {

    fun getRequiredRequestUserId(): String {
        if (requestUserId.isNullOrBlank()) {
            throw MissingRequestUserIdException("requestUserId가 필수로 필요합니다")
        }
        return requestUserId
    }

}
