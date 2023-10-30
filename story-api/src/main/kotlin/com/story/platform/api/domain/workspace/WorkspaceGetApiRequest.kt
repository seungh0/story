package com.story.platform.api.domain.workspace

import com.story.platform.core.domain.workspace.WorkspaceStatus

data class WorkspaceGetApiRequest(
    val filterStatus: WorkspaceStatus? = WorkspaceStatus.ENABLED,
)
