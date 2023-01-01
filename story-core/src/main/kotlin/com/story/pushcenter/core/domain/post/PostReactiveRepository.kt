package com.story.pushcenter.core.domain.post

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface PostReactiveRepository : ReactiveCassandraRepository<Post, PostPrimaryKey>
