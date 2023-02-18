package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.*
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT

@Table(PostTableNames.POST_REVERSE)
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
                accountId = post.accountId,
                postId = post.key.postId,
                spaceType = post.key.spaceType,
                spaceId = post.key.spaceId,
            ),
            title = post.title,
            content = post.content,
            extraJson = post.extraJson,
        )
    }

}


@PrimaryKeyClass
data class PostReversePrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "account_id", type = PARTITIONED, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val accountId: String,

    @field:PrimaryKeyColumn(value = "post_id", type = CLUSTERED, ordering = DESCENDING, ordinal = 3)
    @field:CassandraType(type = BIGINT)
    val postId: Long,

    @field:PrimaryKeyColumn(value = "space_type", type = CLUSTERED, ordering = DESCENDING, ordinal = 4)
    @field:CassandraType(type = TEXT)
    val spaceType: PostSpaceType,

    @field:PrimaryKeyColumn(value = "space_id", type = CLUSTERED, ordering = DESCENDING, ordinal = 5)
    @field:CassandraType(type = TEXT)
    val spaceId: String,
)
