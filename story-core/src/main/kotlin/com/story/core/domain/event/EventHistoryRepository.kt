package com.story.core.domain.event

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface EventHistoryRepository : CassandraBasicRepository<EventHistory, EventHistoryPrimaryKey>
