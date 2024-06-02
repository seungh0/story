package com.story.core.domain.reaction

import org.springframework.stereotype.Repository

@Repository
class ReactionCountEntityRepository(
    private val reactionCountCassandraRepository: ReactionCountCassandraRepository,
) : ReactionCountRepository {

    override suspend fun increase(
        key: ReactionCountKey,
        count: Long,
    ) {
        reactionCountCassandraRepository.increase(
            key = ReactionCountPrimaryKey.from(key),
            count = count,
        )
    }

    override suspend fun decrease(
        key: ReactionCountKey,
        count: Long,
    ) {
        reactionCountCassandraRepository.decrease(
            key = ReactionCountPrimaryKey.from(key),
            count = count,
        )
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
    ): Map<ReactionCountKey, Long> {
        return reactionCountCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
        ).associateBy {
            ReactionCountKey(
                workspaceId = it.key.workspaceId,
                componentId = it.key.componentId,
                spaceId = it.key.spaceId,
                emotionId = it.key.emotionId,
            )
        }.mapValues { it.value.count }
    }

}
