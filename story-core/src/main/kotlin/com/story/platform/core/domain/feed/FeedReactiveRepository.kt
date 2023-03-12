package com.story.platform.core.domain.feed

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface FeedReactiveRepository : ReactiveCassandraRepository<Feed, FeedPrimaryKey>
