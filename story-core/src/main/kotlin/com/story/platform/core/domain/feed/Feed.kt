package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime

/**
 * Feed, Feed_Heavy
 * - Feed
 * - FeedHeavy: 슬롯이 1만개로 분산... (50000 * 10000 = 500,000,000 (5억)
 *
 * Feed (유저별로 보이는 피드...)
 * - service_type
 * - subscriber_id
 * - created_at
 * - space_id
 * - feed_id
 * - title
 * - contents
 *
 * FeedSlot (피드 슬롯 정보)
 * - service_type
 * - target_id: 대상자의 ID
 * - feed_id: 피드 ID
 * - slotCount: 슬롯 갯수
 *
 * FeedSubscriber (피드가 보여질 대상 구독자들)
 * - service_type
 * - space_id
 * - feed_id
 * - slot
 * - subscriber_id
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

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val createdAt: LocalDateTime,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val feedType: FeedType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val feedId: String,
)
