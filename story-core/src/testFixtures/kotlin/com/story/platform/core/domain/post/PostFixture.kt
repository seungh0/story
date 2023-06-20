package com.story.platform.core.domain.post

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.support.RandomGenerator

object PostFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        spaceType: PostSpaceType = RandomGenerator.generateEnum(PostSpaceType::class.java),
        spaceId: String = RandomGenerator.generateString(),
        accountId: String = RandomGenerator.generateString(),
        postId: Long = RandomGenerator.generateLong(),
        title: String = RandomGenerator.generateString(),
        content: String = RandomGenerator.generateString(),
        extraJson: String? = RandomGenerator.generateString(),
    ) = Post(
        key = PostPrimaryKey.of(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            postId = postId,
        ),
        accountId = accountId,
        title = title,
        content = content,
        extraJson = extraJson,
        auditingTime = AuditingTime.newEntity(),
    )

}
