package com.story.core.domain.feed

import com.story.core.support.cassandra.CassandraBasicRepository

interface FeedReverseEntityCassandraRepository : CassandraBasicRepository<FeedReverseEntity, FeedReverseEntityPrimaryKey>
