package com.story.platform.core.domain.workspace

import com.story.platform.core.common.model.dto.AuditingTimeResponse

data class WorkspaceResponse(
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,
    val status: WorkspaceStatus,
) : AuditingTimeResponse() {

    fun isEnabled() = this.status.isEnabled()

    companion object {
        fun of(workspace: Workspace): WorkspaceResponse {
            val response = WorkspaceResponse(
                workspaceId = workspace.workspaceId,
                name = workspace.name,
                plan = workspace.plan,
                status = workspace.status,
            )
            response.from(workspace.auditingTime)
            return response
        }
    }

}
