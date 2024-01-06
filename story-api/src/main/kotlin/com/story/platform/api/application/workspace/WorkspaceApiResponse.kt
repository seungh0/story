package com.story.platform.api.application.workspace

import com.story.platform.core.domain.workspace.WorkspacePricePlan
import com.story.platform.core.domain.workspace.WorkspaceResponse

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
