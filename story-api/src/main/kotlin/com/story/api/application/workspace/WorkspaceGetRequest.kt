package com.story.api.application.workspace

import com.story.core.domain.workspace.WorkspaceStatus

data class WorkspaceGetRequest(
    val filterStatus: WorkspaceStatus? = WorkspaceStatus.ENABLED,
)
