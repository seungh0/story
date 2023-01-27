package com.story.platform.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostCoroutineRepository : CoroutineCrudRepository<Post, PostPrimaryKey>
