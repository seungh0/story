package com.story.pushcenter.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostReverseCoroutineRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey>
