package com.story.platform.api.application.workspace

import com.story.platform.core.domain.workspace.WorkspaceStatus

data class WorkspaceGetApiRequest(
    val filterStatus: WorkspaceStatus? = WorkspaceStatus.ENABLED,
)
