package com.story.core.domain.event

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface EventKeyIdMappingRepository : CassandraBasicRepository<EventKeyIdMapping, EventKeyIdMappingPrimaryKey>
