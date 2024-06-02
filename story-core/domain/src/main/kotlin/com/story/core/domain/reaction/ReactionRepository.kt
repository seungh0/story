package com.story.core.domain.reaction

interface ReactionRepository {

    suspend fun create(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
        emotionIds: Set<String>,
    )

    suspend fun delete(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): Set<String>

    suspend fun findById(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): Reaction?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceIds: Collection<String>,
    ): List<Reaction>

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceId: String,
    ): Reaction?

}
