package com.story.datacenter.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostCoroutineRepository : CoroutineCrudRepository<Post, PostPrimaryKey>
