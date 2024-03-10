package com.story.core.domain.post

import com.story.core.common.model.AuditingTime
import org.apache.commons.lang3.StringUtils
import org.springframework.data.annotation.Transient
import org.springframework.data.cassandra.core.cql.Ordering.DESCENDING
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.Embedded
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

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
) {

    companion object {
        fun of(post: Post) = PostReverse(
            key = PostReversePrimaryKey.of(
                workspaceId = post.key.workspaceId,
                componentId = post.key.componentId,
                ownerId = post.ownerId,
                parentId = post.key.parentPostKey,
                postId = post.key.postId,
                spaceId = post.key.spaceId,
            ),
            slotId = post.key.slotId,
            title = post.title,
            extra = post.extra,
            auditingTime = AuditingTime.created(),
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
    val ownerId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 5)
    val postId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 6)
    val parentKey: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 7)
    val spaceId: String,
) {

    @Transient
    val parentPostKey: PostKey? = with(this.parentKey) {
        if (this.isBlank()) {
            return@with null
        }
        return@with PostKey.parsed(this)
    }

    fun getDepth(): Int {
        if (parentPostKey == null) {
            return 1
        }
        return parentPostKey.depth + 1
    }

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            ownerId: String,
            postId: Long,
            parentId: PostKey?,
            spaceId: String,
        ) = PostReversePrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = PostDistributionKey.makeKey(ownerId),
            ownerId = ownerId,
            postId = postId,
            parentKey = parentId?.serialize() ?: StringUtils.EMPTY,
            spaceId = spaceId,
        )
    }

}
