package com.story.platform.core.domain.feed

enum class FeedSourceType(
    private val description: String,
) {

    NEW_POST(description = "신규 포스트"),
    NEW_FOLLOW(description = "신규 팔로우"),

}
