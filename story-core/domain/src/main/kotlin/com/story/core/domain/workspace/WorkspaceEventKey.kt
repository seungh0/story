package com.story.core.domain.workspace

import com.story.core.domain.event.EventKey

data class WorkspaceEventKey(
    val workspaceId: String,
) : EventKey {

    override fun makeKey(): String = "workspace:$workspaceId"

}
