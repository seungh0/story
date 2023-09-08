package com.story.platform.core.common.http

enum class HttpHeader(
    val header: String,
    private val description: String,
) {

    X_STORY_API_KEY(header = "X-Story-Api-Key", description = "API 키 헤더"),
    X_REQUEST_ID(header = "X-Request-Id", description = "Request-Id"),
    X_STORY_NONCE("X-Story-Nonce", description = "Nonce Key"),

}
