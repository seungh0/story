package com.story.platform.core.domain.post

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface PostReverseReactiveRepository : ReactiveCassandraRepository<PostReverse, PostReversePrimaryKey>
