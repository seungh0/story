package com.story.platform.core.domain.authentication

import com.story.platform.core.support.RandomGenerator

object AuthenticationKeyFixture {

    fun create(
        authenticationKey: String = RandomGenerator.generateString(),
        workspaceId: String = RandomGenerator.generateString(),
        status: AuthenticationStatus = RandomGenerator.generateEnum(AuthenticationStatus::class.java),
        description: String = RandomGenerator.generateString(),
    ) = Authentication(
        key = AuthenticationPrimaryKey(
            authenticationKey = authenticationKey,
        ),
        workspaceId = workspaceId,
        status = status,
        description = description,
    )

}
