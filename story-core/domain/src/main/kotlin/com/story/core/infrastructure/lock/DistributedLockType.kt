package com.story.core.infrastructure.lock

enum class DistributedLockType(
    val prefix: String,
) {

    SUBSCRIBE("subscribe"),
    FEED_MAPPING("feed-mapping"),
    POST("post"),
    ;

}
