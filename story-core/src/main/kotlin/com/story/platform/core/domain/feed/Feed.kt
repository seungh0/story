package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

/**
 *  고민 사항: 피드에 노출될 정보를 비정규화로 가지고 있을지.. target 정보를 토대로 받아와서 보여주는 것이 좋을지 고민해야봐야 함...
 *  - 피드 폼이 바뀔때마다 대응하기 위해서는 피드 source
 */
@Table("feed_v1")
data class Feed(
    @field:PrimaryKey
    val key: FeedPrimaryKey,
)

@PrimaryKeyClass
data class FeedPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val accountId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val createdDate: LocalDate,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val createdAt: LocalDateTime,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val sourceType: FeedSourceType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val sourceKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 7)
    val feedId: String,
)
