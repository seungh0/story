package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.RandomGenerator

object PostFixture {

    fun create(
        serviceType: ServiceType = RandomGenerator.generateEnum(ServiceType::class.java),
        spaceType: String = RandomGenerator.generateString(),
        spaceId: String = RandomGenerator.generateString(),
        accountId: String = RandomGenerator.generateString(),
        postId: Long = RandomGenerator.generateLong(),
        title: String = RandomGenerator.generateString(),
        content: String = RandomGenerator.generateString(),
        extraJson: String? = RandomGenerator.generateString(),
    ) = Post(
        key = PostPrimaryKey.of(
            postSpaceKey = PostSpaceKey(
                serviceType = serviceType,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            postId = postId,
        ),
        accountId = accountId,
        title = title,
        content = content,
        extraJson = extraJson,
    )

}
