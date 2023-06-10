package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_reverse_v1")
data class PostReverse(
    @field:PrimaryKey
    val key: PostReversePrimaryKey,

    @field:CassandraType(type = BIGINT)
    val slotId: Long,

    @field:CassandraType(type = TEXT)
    val title: String,

    @field:CassandraType(type = TEXT)
    val content: String,

    @field:CassandraType(type = TEXT)
    val extraJson: String?,
) {

    companion object {
        fun of(post: Post) = PostReverse(
            key = PostReversePrimaryKey(
                serviceType = post.key.serviceType,
                accountId = post.accountId,
                postId = post.key.postId,
                spaceType = post.key.spaceType,
                spaceId = post.key.spaceId,
            ),
            slotId = post.key.slotId,
            title = post.title,
            content = post.content,
            extraJson = post.extraJson,
        )
    }

}

@PrimaryKeyClass
data class PostReversePrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val accountId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 3)
    @field:CassandraType(type = BIGINT)
    val postId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 4)
    @field:CassandraType(type = TEXT)
    val spaceType: PostSpaceType,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 5)
    @field:CassandraType(type = TEXT)
    val spaceId: String,
)
