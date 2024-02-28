package com.story.core.domain.post

import com.story.core.common.json.Jsons
import com.story.core.common.model.AuditingTime
import org.apache.commons.lang3.StringUtils
import org.springframework.data.annotation.Transient
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

    val ownerId: String,
    var title: String,
    var extra: MutableMap<String, String> = mutableMapOf(),
    val metadata: MutableMap<PostMetadataType, String> = mutableMapOf(),

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
) {

    fun isOwner(ownerId: String): Boolean {
        return this.ownerId == ownerId
    }

    fun putMetadata(type: PostMetadataType, value: Any) {
        this.metadata[type] = value.toString()
    }

    fun <T> getMetadata(type: PostMetadataType): T {
        val rawMetadata = this.metadata[type]
        if (rawMetadata.isNullOrBlank()) {
            return type.defaultValue as T
        }
        return Jsons.toObject(rawMetadata, type.typedReference)!! as T
    }

    fun patch(
        title: String?,
        extra: Map<String, String>?,
    ): Boolean {
        var hasChanged = false
        if (!title.isNullOrBlank()) {
            hasChanged = hasChanged || this.title != title
            this.title = title
        }

        if (extra != null) {
            hasChanged = hasChanged || this.extra != extra
            this.extra = extra.toMutableMap()
        }

        this.auditingTime.updated()

        return hasChanged
    }

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            parentId: PostKey?,
            ownerId: String,
            postId: Long,
            title: String,
            extra: Map<String, String>,
        ) = Post(
            key = PostPrimaryKey.of(
                postSpaceKey = postSpaceKey,
                postId = postId,
                parentId = parentId,
            ),
            ownerId = ownerId,
            title = title,
            extra = extra.toMutableMap(),
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
    val parentId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 5)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val postId: Long,
) {

    @Transient
    val parentIdKey: PostKey? = if (parentId.isBlank()) null else PostKey.parsed(parentId)

    fun toPostKey() = PostKey(
        spaceId = spaceId,
        postId = postId,
        depth = getDepth(),
        parentId = parentId,
    )

    fun getDepth(): Int {
        if (parentIdKey == null) {
            return 1
        }
        return parentIdKey.depth + 1
    }

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            parentId: PostKey?,
            postId: Long,
        ) = PostPrimaryKey(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        )

        fun from(reverse: PostReverse) = PostPrimaryKey(
            workspaceId = reverse.key.workspaceId,
            componentId = reverse.key.componentId,
            spaceId = reverse.key.spaceId,
            slotId = PostSlotAssigner.assign(postId = reverse.key.postId),
            postId = reverse.key.postId,
            parentId = reverse.key.parentId,
        )
    }

}
