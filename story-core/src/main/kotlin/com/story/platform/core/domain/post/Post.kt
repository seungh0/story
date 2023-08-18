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
    var content: String,

    val extra: MutableMap<String, String> = mutableMapOf(),

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
) {

    fun isOwner(accountId: String): Boolean {
        return this.accountId == accountId
    }

    fun patch(
        title: String?,
        content: String?,
        extra: Map<String, String?>?,
    ): Boolean {
        var hasChanged = false
        if (!title.isNullOrBlank()) {
            hasChanged = hasChanged || this.title != title
            this.title = title
        }

        if (content != null) {
            hasChanged = hasChanged || this.content != content
            this.content = content
        }

        if (extra != null) {
            hasChanged = hasChanged || this.extra != extra
            for ((key, value) in extra) {
                if (value == null) {
                    this.extra.remove(key)
                } else {
                    this.extra[key] = value
                }
            }
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
            content: String,
            extra: Map<String, String>?,
        ) = Post(
            key = PostPrimaryKey.of(
                postSpaceKey = postSpaceKey,
                postId = postId,
            ),
            accountId = accountId,
            title = title,
            content = content,
            extra = extra?.toMutableMap() ?: mutableMapOf(),
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
