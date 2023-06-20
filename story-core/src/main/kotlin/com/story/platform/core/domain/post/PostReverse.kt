package com.story.platform.core.domain.post

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
    val content: String,
    val extraJson: String?,
) {

    companion object {
        fun of(post: Post) = PostReverse(
            key = PostReversePrimaryKey(
                workspaceId = post.key.workspaceId,
                componentId = post.key.componentId,
                accountId = post.accountId,
                postId = post.key.postId,
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
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    val accountId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 4)
    val postId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 5)
    val spaceId: String,
)
