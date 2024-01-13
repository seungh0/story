package com.story.core.domain.authentication

import com.story.core.support.RandomGenerator

object AuthenticationFixture {

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