package com.story.core.common.http

enum class HttpHeader(
    val header: String,
    val description: String,
    val example: String,
) {

    X_STORY_API_KEY(
        header = "X-Story-Api-Key",
        description = "Story Platform 인증 키",
        example = "a6d63e0e-7e5c-4fd8-85f2-fc9eb36fa9bd"
    ),
    X_STORY_REQUEST_USER_ID(
        header = "X-Story-Request-User-Id",
        description = "요청자의 계정 ID",
        "ac6e589e-5411-43c0-a166-d3fe068db77c"
    ),
    X_STORY_NONCE("X-Story-Nonce", description = "논스 토큰 (About Nonce 참고)", "92a41af5-1b70-48db-9948-99b114bce9ee"),

    X_REQUEST_ID(header = "X-Request-Id", description = "Request-Id", "c64af254-d067-475c-b4ce-15a6f0a73f99"),
    X_FORWARDED_FOR(header = "X-Forwarded-For", description = "X-Forwarded-For", "127.0.0.1"),
    ACCEPT_LANGUAGE("Accept-Language", description = "Accept-Language", example = "ko/en"),

}
