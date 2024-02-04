package com.story.core.domain.apikey

import com.story.core.support.RandomGenerator

object ApiKeyFixture {

    fun create(
        apiKey: String = RandomGenerator.generateString(),
        workspaceId: String = RandomGenerator.generateString(),
        status: ApiKeyStatus = RandomGenerator.generateEnum(ApiKeyStatus::class.java),
        description: String = RandomGenerator.generateString(),
    ) = ApiKey.of(
        apiKey = apiKey,
        workspaceId = workspaceId,
        status = status,
        description = description,
    )

}
