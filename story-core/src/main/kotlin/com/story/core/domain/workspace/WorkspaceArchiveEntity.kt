package com.story.core.domain.workspace

import com.story.core.common.model.AuditingTimeEntity
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Duration
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

    fun toWorkspace() = WorkspaceEntity(
        workspaceId = workspaceId,
        name = name,
        plan = plan,
        status = WorkspaceStatus.DELETED,
        auditingTime = auditingTime,
    )

    fun canPurge(now: LocalDateTime): Boolean = this.archiveTime <= now.minus(MIN_RETENTION_DURATION)

    companion object {
        private val MIN_RETENTION_DURATION = Duration.ofDays(90)

        fun from(workspace: WorkspaceEntity, archiveTime: LocalDateTime = LocalDateTime.now()) = WorkspaceArchiveEntity(
            workspaceId = workspace.workspaceId,
            name = workspace.name,
            plan = workspace.plan,
            auditingTime = workspace.auditingTime,
            archiveTime = archiveTime,
        )
    }

}
