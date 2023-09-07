package com.story.platform.core.domain.reaction

data class ReactionDeleteResponse(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val accountId: String,
    val deletedOptionIds: Set<String>,
) {

    companion object {
        fun deleted(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            accountId: String,
            deletedOptionIds: Set<String>,
        ) = ReactionDeleteResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            deletedOptionIds = deletedOptionIds,
        )
    }

}
