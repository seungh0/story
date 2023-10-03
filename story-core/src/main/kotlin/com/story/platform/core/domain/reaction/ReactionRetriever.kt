package com.story.platform.core.domain.reaction

import com.story.platform.core.common.distribution.XLargeDistributionKey
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
        accountId: String?,
        emotionIds: Set<String>,
    ): ReactionResponse {
        val reactionCountMap = reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyEmotionIdIn(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotionIds = emotionIds,
        ).associateBy { it.key }

        val spaceIdReaction = accountId?.let {
            reactionReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyDistributionKeyAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                accountId = accountId,
                distributionKey = XLargeDistributionKey.makeKey(spaceId).key,
                spaceId = spaceId,
            )
        }

        return ReactionResponse(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotions = emotionIds.map { emotionId ->
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
                    reactedByMe = spaceIdReaction != null && spaceIdReaction.emotionIds.contains(emotionId),
                )
            }
        )
    }

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        spaceIds: Set<String>,
        accountId: String?,
        emotionIds: Set<String>,
    ): List<ReactionResponse> {
        val reactionCountMap = spaceIds.flatMap { spaceId ->
            reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyEmotionIdIn(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionIds = emotionIds,
            )
        }.associateBy { it.key }

        val spaceIdReactionMap = accountId?.let {
            val distributionKeyTargetIdMap = spaceIds.groupBy { targetId -> XLargeDistributionKey.makeKey(targetId).key }

            return@let distributionKeyTargetIdMap.flatMap { (distributionKey, spaceIds) ->
                reactionReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyDistributionKeyAndKeySpaceIdIn(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    accountId = accountId,
                    distributionKey = distributionKey,
                    spaceIds = spaceIds,
                )
            }.associateBy { it.key.spaceId }
        } ?: emptyMap()

        return spaceIds.map { spaceId ->
            val accountReaction = spaceIdReactionMap[spaceId]
            ReactionResponse(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotions = emotionIds.map { emotionId ->
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
                        reactedByMe = accountReaction != null && accountReaction.emotionIds.contains(emotionId),
                    )
                }
            )
        }
    }

}
