package com.story.core.domain.apikey

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.support.RandomGenerator.generateEnum
import com.story.core.support.RandomGenerator.generateString

object WorkspaceApiKeyEntityFixture {

    fun create(
        workspaceId: String = generateString(),
        apiKey: String = generateString(),
        description: String = generateString(),
        status: ApiKeyStatus = generateEnum(ApiKeyStatus::class.java),
    ) = WorkspaceApiKeyEntity(
        key = WorkspaceApiKeyPrimaryKey(
            workspaceId = workspaceId,
            apiKey = apiKey,
        ),
        description = description,
        status = status,
        auditingTime = AuditingTimeEntity.created(),
    )

}
