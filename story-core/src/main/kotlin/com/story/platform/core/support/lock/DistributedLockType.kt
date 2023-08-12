package com.story.platform.core.support.lock

enum class DistributedLockType(
    val prefix: String,
) {

    SUBSCRIBE("subscribe"),
    FEED_MAPPING_CONNECT("feed_mapping_connect"),

}
