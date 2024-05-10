package com.story.core.domain.workspace

import com.story.core.common.model.dto.AuditingTimeResponse

data class Workspace(
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,
    val status: WorkspaceStatus,
) : AuditingTimeResponse() {

    fun isEnabled() = this.status.isEnabled()

    companion object {
        fun of(workspace: WorkspaceEntity): Workspace {
            val response = Workspace(
                workspaceId = workspace.workspaceId,
                name = workspace.name,
                plan = workspace.plan,
                status = workspace.status,
            )
            response.setAuditingTime(workspace.auditingTime)
            return response
        }
    }

}
