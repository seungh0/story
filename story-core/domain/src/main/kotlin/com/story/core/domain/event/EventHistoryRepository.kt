package com.story.core.domain.event

import com.story.core.support.cassandra.CassandraBasicRepository

interface EventHistoryRepository : CassandraBasicRepository<EventHistoryEntity, EventHistoryPrimaryKey>
