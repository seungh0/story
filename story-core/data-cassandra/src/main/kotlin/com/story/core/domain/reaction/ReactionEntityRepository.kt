package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Repository

@Repository
class ReactionEntityRepository(
    private val reactionCassandraRepository: ReactionCassandraRepository,
    private val reactionReverseCassandraRepository: ReactionReverseCassandraRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) : ReactionRepository {

    override suspend fun create(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
        emotionIds: Set<String>,
    ) {
        val newReaction = ReactionEntity.of(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
            emotionIds = emotionIds,
        )
        reactiveCassandraOperations.batchOps()
            .upsert(newReaction)
            .upsert(ReactionReverseEntity.from(newReaction))
            .executeCoroutine()
    }

    override suspend fun delete(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): Set<String> {
        val reaction = reactionCassandraRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
            )
        ) ?: return emptySet()
        reactiveCassandraOperations.batchOps()
            .delete(reaction)
            .delete(ReactionReverseEntity.from(reaction))
            .executeCoroutine()

        return reaction.emotionIds
    }

    override suspend fun findById(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        userId: String,
    ): Reaction? {
        return reactionCassandraRepository.findById(
            ReactionPrimaryKey.of(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
            )
        )?.toReaction()
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceIds: Collection<String>,
    ): List<Reaction> {
        return reactionReverseCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
            workspaceId = workspaceId,
            componentId = componentId,
            userId = userId,
            distributionKey = distributionKey,
            spaceIds = spaceIds,
        ).map { it.toReaction() }
    }

    override suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        userId: String,
        distributionKey: String,
        spaceId: String,
    ): Reaction? {
        return reactionReverseCassandraRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
            workspaceId = workspaceId,
            componentId = componentId,
            userId = userId,
            distributionKey = ReactionDistributionKey.makeKey(spaceId),
            spaceId = spaceId,
        )?.toReaction()
    }

}
