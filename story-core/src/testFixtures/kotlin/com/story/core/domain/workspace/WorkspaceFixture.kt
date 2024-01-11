package com.story.core.domain.workspace

import com.story.core.common.model.AuditingTime
import com.story.core.support.RandomGenerator
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
