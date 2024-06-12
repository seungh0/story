package com.story.core.domain.post.section

import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostSpaceKey
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_section_v1")
data class PostSectionEntity(
    @field:PrimaryKey
    val key: PostSectionPrimaryKey,
    val sectionType: PostSectionType,
    val data: String,
) {

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            parentId: PostId?,
            postNo: Long,
            priority: Long,
            sectionType: PostSectionType,
            content: PostSectionContentEntity,
        ) = PostSectionEntity(
            key = PostSectionPrimaryKey(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = parentId?.serialize() ?: StringUtils.EMPTY,
                slotId = PostSectionSlotAssigner.assign(postId = postNo),
                postNo = postNo,
                priority = priority,
            ),
            sectionType = sectionType,
            data = content.makeData(),
        )
    }

}

@PrimaryKeyClass
data class PostSectionPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val spaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val parentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 5)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val postNo: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING, ordinal = 7)
    val priority: Long,
)