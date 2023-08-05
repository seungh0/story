package com.story.platform.core.domain.authentication

import com.story.platform.core.support.RandomGenerator

object AuthenticationKeyFixture {

    fun create(
        authenticationKey: String = RandomGenerator.generateString(),
        workspaceId: String = RandomGenerator.generateString(),
        status: AuthenticationKeyStatus = RandomGenerator.generateEnum(AuthenticationKeyStatus::class.java),
        description: String = RandomGenerator.generateString(),
    ) = AuthenticationKey(
        key = AuthenticationKeyPrimaryKey(
            authenticationKey = authenticationKey,
        ),
        workspaceId = workspaceId,
        status = status,
        description = description,
    )

}
