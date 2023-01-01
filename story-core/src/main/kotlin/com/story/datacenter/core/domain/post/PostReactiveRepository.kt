package com.story.datacenter.core.domain.post

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface PostReactiveRepository : ReactiveCassandraRepository<Post, PostPrimaryKey>
