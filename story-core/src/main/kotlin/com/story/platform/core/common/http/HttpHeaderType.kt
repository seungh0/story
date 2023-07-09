package com.story.platform.core.common.http

enum class HttpHeaderType(
    val header: String,
    private val description: String,
) {

    X_STORY_AUTHENTICATION_KEY(header = "X-Story-Authentication-Key", description = "인증 키 헤더"),
    X_REQUEST_ID(header = "X-Request-Id", description = "Request-Id"),

}
