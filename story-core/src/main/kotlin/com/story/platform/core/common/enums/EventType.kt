package com.story.platform.core.common.enums

enum class EventType(
    private val description: String,
) {

    POST_CREATED(description = "포스트 생성"),
    POST_UPDATED(description = "포스트 수정"),
    POST_DELETED(description = "포스트 삭제"),
    SUBSCRIPTION_CREATED(description = "구독 생성"),
    SUBSCRIPTION_DELETED(description = "구독 취소"),
    FEED_PUBLISH(description = "피드 발행"),

}
