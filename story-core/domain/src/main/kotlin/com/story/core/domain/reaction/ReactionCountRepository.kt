package com.story.core.domain.reaction

interface ReactionCountRepository {

    suspend fun increase(
        key: ReactionCountKey,
        count: Long = 1L,
    )

    suspend fun decrease(
        key: ReactionCountKey,
        count: Long = 1L,
    )

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
    ): Map<ReactionCountKey, Long>

}
