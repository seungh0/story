package com.story.core.domain.post

import com.story.core.common.json.Jsons
import com.story.core.common.model.AuditingTimeEntity
import com.story.core.domain.post.section.PostSectionContent
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
data class PostEntity(
    @field:PrimaryKey
    val key: PostPrimaryKey,

    val ownerId: String,
    var title: String,
    var extra: MutableMap<String, String> = mutableMapOf(),
    val metadata: MutableMap<PostMetadataType, String> = mutableMapOf(),

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTimeEntity,
) {

    fun toPost() = Post(
        workspaceId = this.key.workspaceId,
        componentId = this.key.componentId,
        spaceId = this.key.spaceId,
        parentId = this.key.parentPostId,
        postId = this.key.postId,
        depth = this.key.getDepth(),
        ownerId = this.ownerId,
        title = this.title,
        extra = this.extra,
        metadata = metadata,
    ).apply { this.setAuditingTime(auditingTime) }

    fun isOwner(ownerId: String): Boolean {
        return this.ownerId == ownerId
    }

    fun toPostWithSections(sections: List<PostSectionContent>): PostWithSections {
        val response = PostWithSections(
            workspaceId = this.key.workspaceId,
            componentId = this.key.componentId,
            spaceId = this.key.spaceId,
            parentId = this.key.parentPostId,
            postId = this.key.postId,
            depth = this.key.getDepth(),
            ownerId = this.ownerId,
            title = this.title,
            sections = sections,
            extra = this.extra,
            metadata = this.toPostMeta(),
        )
        response.setAuditingTime(this.auditingTime)
        return response
    }

    fun toPostMeta() = PostMetadata(
        hasChildren = getMetadata(type = PostMetadataType.HAS_CHILDREN),
    )

    private fun <T> getMetadata(type: PostMetadataType): T {
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
            workspaceId: String,
            componentId: String,
            spaceId: String,
            parentId: PostId?,
            ownerId: String,
            postNo: Long,
            title: String,
            extra: Map<String, String>,
        ) = PostEntity(
            key = PostPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                postNo = postNo,
                parentId = parentId,
            ),
            ownerId = ownerId,
            title = title,
            extra = extra.toMutableMap(),
            auditingTime = AuditingTimeEntity.created(),
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
    val postNo: Long,
) {

    @Transient
    val parentPostId: PostId? = if (parentId.isBlank()) null else PostId.parsed(parentId)

    @Transient
    val postId = PostId(
        spaceId = spaceId,
        postNo = postNo,
        depth = getDepth(),
        parentId = parentId,
    )

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
            spaceId: String,
            parentId: PostId?,
            postNo: Long,
        ) = PostPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            parentId = parentId?.serialize() ?: StringUtils.EMPTY,
            slotId = PostSlotAssigner.assign(postNo),
            postNo = postNo,
        )

        fun from(reverse: PostReverse) = PostPrimaryKey(
            workspaceId = reverse.key.workspaceId,
            componentId = reverse.key.componentId,
            spaceId = reverse.key.spaceId,
            slotId = PostSlotAssigner.assign(postNo = reverse.key.postNo),
            postNo = reverse.key.postNo,
            parentId = reverse.key.parentId,
        )
    }

}
