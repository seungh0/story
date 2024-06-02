package com.story.core.domain.workspace

import com.story.core.common.model.AuditingTimeEntity
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime

@Table("workspace_archive_v1")
data class WorkspaceArchiveEntity(
    @field:PrimaryKey
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTimeEntity,
    val archiveTime: LocalDateTime,
) {

    fun toWorkspaceArchive() = WorkspaceArchive(
        workspaceId = workspaceId,
        name = name,
        plan = plan,
        archiveTime = archiveTime,
    ).apply { setAuditingTime(auditingTime) }

    fun toWorkspaceEntity() = WorkspaceEntity(
        workspaceId = workspaceId,
        name = name,
        plan = plan,
        status = WorkspaceStatus.DELETED,
        auditingTime = this.auditingTime,
    )

    companion object {
        fun from(workspaceArchive: WorkspaceArchive) = WorkspaceArchiveEntity(
            workspaceId = workspaceArchive.workspaceId,
            name = workspaceArchive.name,
            plan = workspaceArchive.plan,
            auditingTime = AuditingTimeEntity(
                createdAt = workspaceArchive.createdAt,
                updatedAt = workspaceArchive.createdAt
            ),
            archiveTime = workspaceArchive.archiveTime,
        )

        fun from(workspace: WorkspaceEntity, archiveTime: LocalDateTime = LocalDateTime.now()) = WorkspaceArchiveEntity(
            workspaceId = workspace.workspaceId,
            name = workspace.name,
            plan = workspace.plan,
            auditingTime = workspace.auditingTime,
            archiveTime = archiveTime,
        )
    }

}
