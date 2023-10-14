package com.story.platform.core.common.http

enum class HttpHeader(
    val header: String,
    private val description: String,
) {

    X_STORY_API_KEY(header = "X-Story-Api-Key", description = "Authentication Key"),
    X_REQUEST_ID(header = "X-Request-Id", description = "Request-Id"),
    X_FORWARDED_FOR(header = "X-Forwarded-For", description = "X-Forwarded-For"),
    X_STORY_REQUEST_ACCOUNT_ID(header = "X-Story-Request-Account-Id", description = "Request-Account-Id"),
    X_STORY_NONCE("X-Story-Nonce", description = "Nonce Key"),

}
