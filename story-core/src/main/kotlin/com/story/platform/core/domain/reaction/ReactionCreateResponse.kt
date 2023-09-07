package com.story.platform.core.domain.reaction

data class ReactionCreateResponse(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
    val accountId: String,
    val createdOptionIds: Set<String>,
    val deletedOptionIds: Set<String>,
    val totalEmotionsCount: Long,
) {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            createdOptionIds: Set<String>,
            totalEmotionsCount: Long,
        ) = ReactionCreateResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = emptySet(),
            totalEmotionsCount = totalEmotionsCount,
        )

        fun updated(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            createdOptionIds: Set<String>,
            deletedOptionIds: Set<String>,
            totalEmotionsCount: Long,
        ) = ReactionCreateResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            createdOptionIds = createdOptionIds,
            deletedOptionIds = deletedOptionIds,
            totalEmotionsCount = totalEmotionsCount,
        )
    }

}
