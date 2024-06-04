package com.story.core.domain.post

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.common.model.dto.AuditingTimeResponse
import java.time.LocalDateTime

data class Post(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val parentId: PostId?,
    val postId: PostId,
    val depth: Int,
    val ownerId: String,
    var title: String,
    var extra: Map<String, String>,
    val metadata: Map<PostMetadataType, String>,
) : AuditingTimeResponse() {

    fun isOwner(ownerId: String): Boolean {
        return this.ownerId == ownerId
    }

    fun patch(title: String?, extra: Map<String, String>?): Boolean {
        var hasChanged = false
        if (!title.isNullOrBlank()) {
            hasChanged = hasChanged || this.title != title
            this.title = title
        }

        if (extra != null) {
            hasChanged = hasChanged || this.extra != extra
            this.extra = extra.toMutableMap()
        }

        this.setAuditingTime(AuditingTimeEntity(super.createdAt, LocalDateTime.now()))

        return hasChanged
    }

}
