package com.story.platform.core.domain.event

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EventHistoryReverseRepository : CoroutineCrudRepository<EventHistoryReverse, EventHistoryReversePrimaryKey>
