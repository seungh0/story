package com.story.platform.core.domain.post.section

import com.story.platform.core.domain.post.PostSpaceKey
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_section_v1")
data class PostSection(
    @field:PrimaryKey
    val key: PostSectionPrimaryKey,
    val sectionType: PostSectionType,
    val data: String,
) {

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            postId: Long,
            priority: Long,
            sectionType: PostSectionType,
            content: PostSectionContent,
        ) = PostSection(
            key = PostSectionPrimaryKey(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                slotId = PostSectionSlotAssigner.assign(postId = postId),
                postId = postId,
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
    val slotId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val postId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING, ordinal = 6)
    val priority: Long,
)
