package com.story.api.application.workspace

import com.story.core.domain.workspace.Workspace
import com.story.core.domain.workspace.WorkspacePricePlan

data class WorkspaceApiResponse(
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,
) {

    companion object {
        fun of(workspace: Workspace) = WorkspaceApiResponse(
            workspaceId = workspace.workspaceId,
            name = workspace.name,
            plan = workspace.plan,
        )
    }

}
