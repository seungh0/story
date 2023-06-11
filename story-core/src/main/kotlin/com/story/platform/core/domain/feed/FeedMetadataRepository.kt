package com.story.platform.core.domain.feed

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedMetadataRepository : CoroutineCrudRepository<FeedMetadata, FeedMetadataPrimaryKey>
