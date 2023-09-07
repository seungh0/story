package com.story.platform.core.domain.reaction

data class ReactionDeleteResponse(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
    val accountId: String,
    val deletedOptionIds: Set<String>,
) {

    companion object {
        fun deleted(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            deletedOptionIds: Set<String>,
        ) = ReactionDeleteResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            deletedOptionIds = deletedOptionIds,
        )
    }

}
