package com.story.core.domain.apikey

import com.story.core.common.model.AuditingTime
import com.story.core.support.RandomGenerator.generateEnum
import com.story.core.support.RandomGenerator.generateString

object WorkspaceApiKeyFixture {

    fun create(
        workspaceId: String = generateString(),
        apiKey: String = generateString(),
        description: String = generateString(),
        status: ApiKeyStatus = generateEnum(ApiKeyStatus::class.java),
    ) = WorkspaceApiKey(
        key = WorkspaceApiKeyPrimaryKey(
            workspaceId = workspaceId,
            apiKey = apiKey,
        ),
        description = description,
        status = status,
        auditingTime = AuditingTime.created(),
    )

}
