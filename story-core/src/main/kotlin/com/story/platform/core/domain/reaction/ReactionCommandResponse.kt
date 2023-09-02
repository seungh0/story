package com.story.platform.core.domain.reaction

data class ReactionCommandResponse(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
    val accountId: String,
    val createdOptionIds: Set<String>,
    val deletedOptionIds: Set<String>,
) {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            createdOptionIds: Set<String>,
        ) = ReactionCommandResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = emptySet(),
        )

        fun updated(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            createdOptionIds: Set<String>,
            deletedOptionIds: Set<String>,
        ) = ReactionCommandResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
        )

        fun deleted(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            deletedOptionIds: Set<String>,
        ) = ReactionCommandResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            createdOptionIds = emptySet(),
            deletedOptionIds = deletedOptionIds,
        )
    }

}
