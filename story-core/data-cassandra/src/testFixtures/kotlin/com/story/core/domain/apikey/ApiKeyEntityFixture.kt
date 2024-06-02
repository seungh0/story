package com.story.core.domain.apikey

import com.story.core.support.RandomGenerator

object ApiKeyEntityFixture {

    fun create(
        apiKey: String = RandomGenerator.generateString(),
        workspaceId: String = RandomGenerator.generateString(),
        status: ApiKeyStatus = RandomGenerator.generateEnum(ApiKeyStatus::class.java),
        description: String = RandomGenerator.generateString(),
    ) = ApiKeyEntity.of(
        apiKey = apiKey,
        workspaceId = workspaceId,
        status = status,
        description = description,
    )

}
