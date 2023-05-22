package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.post.PostSlotAssigner
import com.story.platform.core.domain.post.PostSpaceType
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.support.json.toJson
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(FeedTableNames.FEED)
data class Feed(
    @field:PrimaryKey
    val key: FeedPrimaryKey,

    @field:Column(value = "feed_type")
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedType: FeedType,

    @field:Column(value = "feed_key")
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val feedKeyJson: String,
) {

    companion object {
        fun fromPost(
            serviceType: ServiceType,
            accountId: String,
            spaceType: PostSpaceType,
            spaceId: String,
            postId: String,
        ) = Feed(
            key = FeedPrimaryKey(
                serviceType = serviceType,
                accountId = accountId,
            ),
            feedType = FeedType.POST,
            feedKeyJson = FeedPostKey(
                spaceType = spaceType,
                spaceId = spaceId,
                slotId = PostSlotAssigner.assign(postId = postId),
                postId = postId,
            ).toJson(),
        )

        fun fromSubscription(
            serviceType: ServiceType,
            accountId: String,
            subscriptionType: SubscriptionType,
            targetId: String,
            subscriberId: String,
        ) = Feed(
            key = FeedPrimaryKey(
                serviceType = serviceType,
                accountId = accountId,
            ),
            feedType = FeedType.SUBSCRIPTION,
            feedKeyJson = FeedSubscriptionKey(
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            ).toJson(),
        )
    }

}

@PrimaryKeyClass
data class FeedPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "account_id", type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    @field:CassandraType(type = CassandraType.Name.TEXT)
    val accountId: String,
)
