package com.story.platform.core.domain.workspace

enum class WorkspaceStatus(
    private val isActivated: Boolean,
) {

    ENABLED(isActivated = true),
    DELETED(isActivated = false),
    ;

    fun isEnabled(): Boolean = this.isActivated

}
