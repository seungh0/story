package com.story.platform.core.domain.feed

import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface FeedRepository : CoroutineCrudRepository<Feed, FeedPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyCreatedDateAndKeyCreatedAtLessThan(
        workspaceId: String,
        componentId: String,
        accountId: String,
        createdDate: LocalDate,
        createdAt: LocalDateTime,
    ): Slice<Feed>

}
