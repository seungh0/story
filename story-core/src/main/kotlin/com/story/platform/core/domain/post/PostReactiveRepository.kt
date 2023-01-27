package com.story.platform.core.domain.post

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface PostReactiveRepository : ReactiveCassandraRepository<Post, PostPrimaryKey>
