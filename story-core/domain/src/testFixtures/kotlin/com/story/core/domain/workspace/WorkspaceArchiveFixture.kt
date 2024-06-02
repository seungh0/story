package com.story.core.domain.workspace

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.support.RandomGenerator
import java.time.LocalDateTime

object WorkspaceArchiveFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        name: String = RandomGenerator.generateString(),
        plan: WorkspacePricePlan = RandomGenerator.generateEnum(WorkspacePricePlan::class.java),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now(),
        archiveTime: LocalDateTime = LocalDateTime.now(),
    ) = WorkspaceArchive(
        workspaceId = workspaceId,
        name = name,
        plan = plan,
        archiveTime = archiveTime,
    ).apply { this.setAuditingTime(AuditingTimeEntity(createdAt = createdAt, updatedAt = updatedAt)) }

}
