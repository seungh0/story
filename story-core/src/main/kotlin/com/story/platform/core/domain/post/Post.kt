package com.story.platform.core.domain.post

import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_v1")
data class Post(
    @field:PrimaryKey
    val key: PostPrimaryKey,

    val accountId: String,
    var title: String,
    val extra: MutableMap<String, String> = mutableMapOf(),

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
) {

    fun isOwner(accountId: String): Boolean {
        return this.accountId == accountId
    }

    fun patch(
        title: String?,
    ): Boolean {
        var hasChanged = false
        if (!title.isNullOrBlank()) {
            hasChanged = hasChanged || this.title != title
            this.title = title
        }

        this.auditingTime.updated()

        return hasChanged
    }

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            accountId: String,
            postId: Long,
            title: String,
        ) = Post(
            key = PostPrimaryKey.of(
                postSpaceKey = postSpaceKey,
                postId = postId,
            ),
            accountId = accountId,
            title = title,
            auditingTime = AuditingTime.created(),
        )
    }

}

@PrimaryKeyClass
data class PostPrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    val spaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 4)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val postId: Long,
) {

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            postId: Long,
        ) = PostPrimaryKey(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        )

        fun from(postReverse: PostReverse) = PostPrimaryKey(
            workspaceId = postReverse.key.workspaceId,
            componentId = postReverse.key.componentId,
            spaceId = postReverse.key.spaceId,
            slotId = PostSlotAssigner.assign(postId = postReverse.key.postId),
            postId = postReverse.key.postId,
        )
    }

}
