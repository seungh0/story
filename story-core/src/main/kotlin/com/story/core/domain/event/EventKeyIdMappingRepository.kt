package com.story.core.domain.event

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EventKeyIdMappingRepository : CoroutineCrudRepository<EventKeyIdMapping, EventKeyIdMappingPrimaryKey>
