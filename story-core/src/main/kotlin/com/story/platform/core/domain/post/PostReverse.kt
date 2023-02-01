package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_reverse_v1")
data class PostReverse(
    @field:PrimaryKey
    val key: PostReversePrimaryKey,

    @field:Column(value = "title")
    @field:CassandraType(type = TEXT)
    val title: String,

    @field:Column(value = "content")
    @field:CassandraType(type = TEXT)
    val content: String,

    @field:Column(value = "extra_json")
    @field:CassandraType(type = TEXT)
    val extraJson: String?,
) {

    companion object {
        fun of(post: Post) = PostReverse(
            key = PostReversePrimaryKey(
                serviceType = post.key.serviceType,
                accountId = post.key.accountId,
                slotId = PostSlotAllocator.allocate(post.key.postId),
                spaceId = post.key.spaceId,
                spaceType = post.key.spaceType,
                postId = post.key.postId,
            ),
            title = post.title,
            content = post.content,
            extraJson = post.extraJson,
        )
    }

}


@PrimaryKeyClass
data class PostReversePrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "account_id", type = PARTITIONED)
    @field:CassandraType(type = TEXT)
    val accountId: String,

    @field:PrimaryKeyColumn(value = "slot_id", type = PARTITIONED)
    @field:CassandraType(type = BIGINT)
    val slotId: Long,

    @field:PrimaryKeyColumn(value = "space_type", type = CLUSTERED)
    @field:CassandraType(type = TEXT)
    val spaceType: String,

    @field:PrimaryKeyColumn(value = "space_id", type = CLUSTERED)
    @field:CassandraType(type = TEXT)
    val spaceId: String,

    @field:PrimaryKeyColumn(value = "post_id", type = CLUSTERED)
    @field:CassandraType(type = TEXT)
    val postId: Long,
)
