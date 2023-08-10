package com.story.platform.core.domain.authentication

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateString

object WorkspaceAuthenticationKeyFixture {

    fun create(
        workspaceId: String = generateString(),
        apiKey: String = generateString(),
        description: String = generateString(),
        status: AuthenticationStatus = generateEnum(AuthenticationStatus::class.java),
    ) = WorkspaceAuthentication(
        key = WorkspaceAuthenticationPrimaryKey(
            workspaceId = workspaceId,
            authenticationKey = apiKey,
        ),
        description = description,
        status = status,
        auditingTime = AuditingTime.created(),
    )

}
