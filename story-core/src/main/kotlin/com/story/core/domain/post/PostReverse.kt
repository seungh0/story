package com.story.core.domain.post

import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_reverse_v1")
data class PostReverse(
    @field:PrimaryKey
    val key: PostReversePrimaryKey,

    val slotId: Long,
    val title: String,
    val extra: MutableMap<String, String> = mutableMapOf(),
) {

    companion object {
        fun of(post: Post) = PostReverse(
            key = PostReversePrimaryKey.of(
                workspaceId = post.key.workspaceId,
                componentId = post.key.componentId,
                accountId = post.accountId,
                postId = post.key.postId,
                spaceId = post.key.spaceId,
            ),
            slotId = post.key.slotId,
            title = post.title,
            extra = post.extra,
        )
    }

}

@PrimaryKeyClass
data class PostReversePrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    val distributionKey: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 4)
    val accountId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 5)
    val postId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 6)
    val spaceId: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            accountId: String,
            postId: Long,
            spaceId: String,
        ) = PostReversePrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = PostDistributionKey.makeKey(accountId), accountId = accountId,
            postId = postId,
            spaceId = spaceId,
        )
    }

}
