package com.story.platform.core.domain.workspace

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.support.RandomGenerator
import java.time.LocalDateTime

object WorkspaceFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        name: String = RandomGenerator.generateString(),
        plan: WorkspacePricePlan = RandomGenerator.generateEnum(WorkspacePricePlan::class.java),
        status: WorkspaceStatus = RandomGenerator.generateEnum(WorkspaceStatus::class.java),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now(),
    ) = Workspace(
        workspaceId = workspaceId,
        name = name,
        plan = plan,
        status = status,
        auditingTime = AuditingTime(createdAt = createdAt, updatedAt = updatedAt),
    )

}
