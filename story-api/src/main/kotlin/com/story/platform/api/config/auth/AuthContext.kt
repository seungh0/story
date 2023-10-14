package com.story.platform.api.config.auth

import com.story.platform.core.common.error.MissingRequestAccountIdException

data class AuthContext(
    val workspaceId: String,
    val requestId: String,
    val requestAccountId: String?,
) {

    fun getRequiredRequestAccountId(): String {
        if (requestAccountId.isNullOrBlank()) {
            throw MissingRequestAccountIdException("requestAccountId가 필수로 필요합니다")
        }
        return requestAccountId
    }

}
