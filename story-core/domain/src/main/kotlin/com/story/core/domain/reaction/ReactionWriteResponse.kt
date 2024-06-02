package com.story.core.domain.reaction

data class ReactionWriteResponse(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val userId: String,
    val createdOptionIds: Set<String>,
    val deletedOptionIds: Set<String>,
) {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            actorId: String,
            createdOptionIds: Set<String>,
        ) = ReactionWriteResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = actorId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = emptySet(),
        )

        fun updated(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            actorId: String,
            createdOptionIds: Set<String>,
            deletedOptionIds: Set<String>,
        ) = ReactionWriteResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = actorId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
        )

        fun deleted(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            actorId: String,
            deletedOptionIds: Set<String>,
        ) = ReactionWriteResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = actorId,
            createdOptionIds = emptySet(),
            deletedOptionIds = deletedOptionIds,
        )
    }

}
