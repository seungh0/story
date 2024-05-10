package com.story.core.domain.reaction

import com.story.core.common.utils.mapToSet
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    ): Reaction {
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

        return Reaction(
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
    ): List<Reaction> = coroutineScope {
        val reactionCounts = spaceIds.map { spaceId ->
            async {
                reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    spaceId = spaceId,
                )
            }
        }

        val spaceIdReactionMap = requestUserId?.let {
            val distributionKeyTargetIdMap = spaceIds.groupBy { targetId -> ReactionDistributionKey.makeKey(targetId) }

            return@let distributionKeyTargetIdMap.map { (distributionKey, spaceIds) ->
                async {
                    reactionReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        userId = requestUserId,
                        distributionKey = distributionKey,
                        spaceIds = spaceIds,
                    )
                }
            }.awaitAll().flatten().associateBy { it.key.spaceId }
        } ?: emptyMap()

        val reactionCountMap = reactionCounts.awaitAll().flatten().associateBy { it.key }

        val emotionIds = reactionCountMap.values.mapToSet { reactionCount -> reactionCount.key.emotionId }

        return@coroutineScope spaceIds.map { spaceId ->
            val reactionByRequestUserId = spaceIdReactionMap[spaceId]
            Reaction(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotions = emotionIds.asSequence()
                    .filter { emotionId ->
                        (
                            reactionCountMap[
                                ReactionCountPrimaryKey(
                                    workspaceId = workspaceId,
                                    componentId = componentId,
                                    spaceId = spaceId,
                                    emotionId = emotionId,
                                )
                            ]?.count ?: 0L
                            ) > 0
                    }
                    .map { emotionId ->
                        ReactionEmotionResponse.of(
                            emotionId = emotionId,
                            count = reactionCountMap[
                                ReactionCountPrimaryKey(
                                    workspaceId = workspaceId,
                                    componentId = componentId,
                                    emotionId = emotionId,
                                    spaceId = spaceId,
                                )
                            ]?.count ?: 0L,
                            reactedByMe = reactionByRequestUserId != null &&
                                reactionByRequestUserId.emotionIds.contains(emotionId),
                        )
                    }
                    .toList()
            )
        }
    }

}
