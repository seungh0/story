package com.story.platform.core.domain.post

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.support.RandomGenerator

object PostFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        componentId: String = RandomGenerator.generateString(),
        spaceId: String = RandomGenerator.generateString(),
        accountId: String = RandomGenerator.generateString(),
        postId: Long = RandomGenerator.generateLong(),
        title: String = RandomGenerator.generateString(),
        extra: Map<String, String> = emptyMap(),
        metadata: Map<PostMetadataType, String> = emptyMap(),
    ) = Post(
        key = PostPrimaryKey.of(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId,
        ),
        accountId = accountId,
        title = title,
        extra = extra.toMutableMap(),
        metadata = metadata.toMutableMap(),
        auditingTime = AuditingTime.created(),
    )

}
