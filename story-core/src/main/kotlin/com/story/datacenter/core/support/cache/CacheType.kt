package com.story.datacenter.core.support.cache

import java.time.Duration

enum class CacheType(
    private val description: String,
    val key: String,
    val duration: Duration,
) {

    ;

    object CacheNameConstants {

    }

}