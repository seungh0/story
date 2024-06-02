package com.story.core.domain.workspace

import com.story.core.common.model.AuditingTimeEntity
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("workspace_v1")
data class WorkspaceEntity(
    @field:PrimaryKey
    val workspaceId: String,

    val name: String,
    val plan: WorkspacePricePlan,
    var status: WorkspaceStatus,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTimeEntity,
) {

    fun toWorkspace(): Workspace {
        val response = Workspace(
            workspaceId = this.workspaceId,
            name = this.name,
            plan = this.plan,
            status = this.status,
        )
        response.setAuditingTime(this.auditingTime)
        return response
    }

    fun delete() {
        this.status = WorkspaceStatus.DELETED
    }

    companion object {
        fun of(
            workspaceId: String,
            name: String,
            plan: WorkspacePricePlan,
            status: WorkspaceStatus = WorkspaceStatus.ENABLED,
            auditingTime: AuditingTimeEntity = AuditingTimeEntity.created(),
        ) = WorkspaceEntity(
            workspaceId = workspaceId,
            name = name,
            plan = plan,
            status = status,
            auditingTime = auditingTime,
        )
    }

}
