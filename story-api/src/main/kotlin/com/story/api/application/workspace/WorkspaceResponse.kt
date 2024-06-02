package com.story.api.application.workspace

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.workspace.Workspace
import com.story.core.domain.workspace.WorkspacePricePlan

data class WorkspaceResponse(
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,
) : AuditingTimeResponse() {

    companion object {
        fun of(workspace: Workspace) = WorkspaceResponse(
            workspaceId = workspace.workspaceId,
            name = workspace.name,
            plan = workspace.plan,
        ).apply { setAuditingTime(workspace) }
    }

}
