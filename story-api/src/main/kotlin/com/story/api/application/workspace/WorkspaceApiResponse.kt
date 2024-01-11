package com.story.api.application.workspace

import com.story.core.domain.workspace.WorkspacePricePlan
import com.story.core.domain.workspace.WorkspaceResponse

data class WorkspaceApiResponse(
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,
) {

    companion object {
        fun of(workspace: WorkspaceResponse) = WorkspaceApiResponse(
            workspaceId = workspace.workspaceId,
            name = workspace.name,
            plan = workspace.plan,
        )
    }

}
