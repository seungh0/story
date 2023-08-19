package com.story.platform.core.domain.workspace

import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Duration
import java.time.LocalDateTime

@Table("workspace_archive_v1")
data class WorkspaceArchive(
    @field:PrimaryKey
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
    val archiveTime: LocalDateTime,
) {

    fun toWorkspace() = Workspace(
        workspaceId = workspaceId,
        name = name,
        plan = plan,
        status = WorkspaceStatus.DELETED,
        auditingTime = auditingTime,
    )

    fun canPurge(now: LocalDateTime): Boolean = this.archiveTime >= now.plus(MIN_RETENTION_DURATION)

    companion object {
        private val MIN_RETENTION_DURATION = Duration.ofDays(90)

        fun from(workspace: Workspace) = WorkspaceArchive(
            workspaceId = workspace.workspaceId,
            name = workspace.name,
            plan = workspace.plan,
            auditingTime = workspace.auditingTime,
            archiveTime = LocalDateTime.now(),
        )
    }

}
