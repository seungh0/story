package com.story.platform.core.domain.workspace

import com.story.platform.core.common.model.dto.AuditingTimeResponse

data class WorkspaceResponse(
    val workspaceId: String,
    val name: String,
    val pricePlan: WorkspacePricePlan,
    val status: WorkspaceStatus,
) : AuditingTimeResponse() {

    companion object {
        fun of(workspace: Workspace): WorkspaceResponse {
            val response = WorkspaceResponse(
                workspaceId = workspace.workspaceId,
                name = workspace.name,
                pricePlan = workspace.pricePlan,
                status = workspace.status,
            )
            response.from(workspace.auditingTime)
            return response
        }
    }

}
