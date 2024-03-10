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
                parentId = post.key.parentPostId,
                postNo = post.key.postNo,
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
    val postNo: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 6)
    val parentId: String,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = DESCENDING, ordinal = 7)
    val spaceId: String,
) {

    @Transient
    val postId = PostId(
        spaceId = spaceId,
        postNo = postNo,
        depth = getDepth(),
        parentId = parentId,
    )

    @Transient
    val parentPostId: PostId? = with(this.parentId) {
        if (this.isBlank()) {
            return@with null
        }
        return@with PostId.parsed(this)
    }

    fun getDepth(): Int {
        if (parentPostId == null) {
            return 1
        }
        return parentPostId.depth + 1
    }

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            ownerId: String,
            postNo: Long,
            parentId: PostId?,
            spaceId: String,
        ) = PostReversePrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = PostDistributionKey.makeKey(ownerId),
            ownerId = ownerId,
            postNo = postNo,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            spaceId = spaceId,
        )
    }

}
