package com.story.platform.core.common.enums

enum class HttpHeaderType(
    val header: String,
    private val description: String,
) {

    X_STORY_API_KEY(header = "X-Story-Api-Key", description = "인증 키 헤더"),

}
