package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface FeedRepository : CoroutineCrudRepository<Feed, FeedPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeyAccountIdAndKeyCreatedDateAndKeyCreatedAtLessThan(
        serviceType: ServiceType,
        accountId: String,
        createdDate: LocalDate,
        createdAt: LocalDateTime,
    ): Slice<Feed>

}
