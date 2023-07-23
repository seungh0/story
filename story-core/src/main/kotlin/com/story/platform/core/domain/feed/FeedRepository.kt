package com.story.platform.core.domain.feed

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedRepository : CoroutineCrudRepository<Feed, FeedPrimaryKey>
