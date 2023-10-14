package com.story.platform.core.common.http

enum class HttpHeader(
    val header: String,
    val description: String,
    val example: String,
) {

    X_STORY_API_KEY(header = "X-Story-Api-Key", description = "Authentication Key", "authentication-key"),
    X_REQUEST_ID(header = "X-Request-Id", description = "Request-Id", "request-id"),
    X_FORWARDED_FOR(header = "X-Forwarded-For", description = "X-Forwarded-For", "127.0.0.1"),
    X_STORY_REQUEST_ACCOUNT_ID(header = "X-Story-Request-Account-Id", description = "Request-Account-Id", "account-Id"),
    X_STORY_NONCE("X-Story-Nonce", description = "Nonce Key", "nonce-key"),

}
