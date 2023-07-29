package com.story.platform.core.support.cache

import com.story.platform.core.domain.event.BaseEvent

data class CacheEvictEventRecord<T>(
    val cacheType: CacheType,
    val payload: T,
) : BaseEvent
