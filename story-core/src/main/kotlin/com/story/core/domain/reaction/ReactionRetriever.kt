package com.story.core.domain.reaction

import org.springframework.stereotype.Service

@Service
class ReactionRetriever(
    private val reactionReverseRepository: ReactionReverseRepository,
    private val reactionCountRepository: ReactionCountRepository,
) {

    suspend fun getReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        requestUserId: String?,
    ): ReactionResponse {
        val reactionCountMap = reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
        ).associateBy { it.key }

        val spaceIdReaction = requestUserId?.let {
            reactionReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                userId = requestUserId,
                distributionKey = ReactionDistributionKey.makeKey(spaceId),
                spaceId = spaceId,
            )
        }

        return ReactionResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotions = reactionCountMap.values.asSequence()
                .filter { reactionCount -> reactionCount.count > 0 }
                .map { reactionCount ->
                    ReactionEmotionResponse.of(
                        emotionId = reactionCount.key.emotionId,
                        count = reactionCountMap[
                            ReactionCountPrimaryKey(
                                workspaceId = workspaceId,
                                componentId = componentId,
                                emotionId = reactionCount.key.emotionId,
                                spaceId = spaceId,
                            )
                        ]?.count ?: 0L,
                        reactedByMe = spaceIdReaction != null && spaceIdReaction.emotionIds.contains(reactionCount.key.emotionId),
                    )
                }
                .toList()
        )
    }

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        spaceIds: Set<String>,
        requestUserId: String?,
    ): List<ReactionResponse> {
        val reactionCountMap = spaceIds.flatMap { spaceId ->
            reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            )
        }.associateBy { it.key }

        val spaceIdReactionMap = requestUserId?.let {
            val distributionKeyTargetIdMap = spaceIds.groupBy { targetId -> ReactionDistributionKey.makeKey(targetId) }

            return@let distributionKeyTargetIdMap.flatMap { (distributionKey, spaceIds) ->
                reactionReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    userId = requestUserId,
                    distributionKey = distributionKey,
                    spaceIds = spaceIds,
                )
            }.associateBy { it.key.spaceId }
        } ?: emptyMap()

        return spaceIds.map { spaceId ->
            val reactionByRequestUserId = spaceIdReactionMap[spaceId]
            ReactionResponse(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotions = reactionCountMap.values.asSequence()
                    .filter { reactionCount -> reactionCount.count > 0 }
                    .map { reactionCount ->
                        ReactionEmotionResponse.of(
                            emotionId = reactionCount.key.emotionId,
                            count = reactionCountMap[
                                ReactionCountPrimaryKey(
                                    workspaceId = workspaceId,
                                    componentId = componentId,
                                    emotionId = reactionCount.key.emotionId,
                                    spaceId = spaceId,
                                )
                            ]?.count ?: 0L,
                            reactedByMe = reactionByRequestUserId != null &&
                                reactionByRequestUserId.emotionIds.contains(reactionCount.key.emotionId),
                        )
                    }
                    .toList()
            )
        }
    }

}
