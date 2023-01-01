package com.story.pushcenter.core.domain.post

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface PostReverseReactiveRepository : ReactiveCassandraRepository<PostReverse, PostReversePrimaryKey>
