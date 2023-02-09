package com.story.platform.core.domain.subscription

enum class SubscriptionStatus(
    private val description: String,
) {

    ACTIVE(description = "활성화 중인 상태"),
    DELETED(description = "삭제된 상태"),

}
