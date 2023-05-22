package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table(PostTableNames.POST)
data class Post(
    @field:PrimaryKey
    val key: PostPrimaryKey,

    @field:Column(value = "account_id")
    @field:CassandraType(type = TEXT)
    val accountId: String,

    @field:Column(value = "title")
    @field:CassandraType(type = TEXT)
    var title: String,

    @field:Column(value = "content")
    @field:CassandraType(type = TEXT)
    var content: String,

    @field:Column(value = "extra_json")
    @field:CassandraType(type = TEXT)
    var extraJson: String?,
) {

    fun isOwner(accountId: String): Boolean {
        return this.accountId == accountId
    }

    fun modify(
        title: String,
        content: String,
        extraJson: String?,
    ) {
        this.title = title
        this.content = content
        this.extraJson = extraJson
    }

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            accountId: String,
            postId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = Post(
            key = PostPrimaryKey.of(
                postSpaceKey = postSpaceKey,
                postId = postId,
            ),
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
    }

}

@PrimaryKeyClass
data class PostPrimaryKey(
    @field:PrimaryKeyColumn(value = "service_type", type = PARTITIONED, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(value = "space_type", type = PARTITIONED, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val spaceType: PostSpaceType,

    @field:PrimaryKeyColumn(value = "space_id", type = PARTITIONED, ordinal = 3)
    @field:CassandraType(type = TEXT)
    val spaceId: String,

    @field:PrimaryKeyColumn(value = "slot_id", type = PARTITIONED, ordinal = 4)
    @field:CassandraType(type = BIGINT)
    val slotId: Long,

    @field:PrimaryKeyColumn(value = "post_id", type = CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    @field:CassandraType(type = TEXT)
    val postId: String,
) {

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            postId: String,
        ) = PostPrimaryKey(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        )
    }

}
