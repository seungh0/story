package com.story.platform.core.domain.reaction

import com.story.platform.core.common.distribution.XLargeDistributionKey
import org.springframework.stereotype.Service

@Service
class ReactionRetriever(
    private val reactionCountRepository: ReactionCountRepository,
    private val reactionReverseRepository: ReactionReverseRepository,
) {

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        targetIds: Set<String>,
        accountId: String?,
        optionIds: Set<String>,
    ): List<ReactionResponse> {
        val keys = targetIds.flatMap { targetId ->
            optionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    emotionId = optionId,
                    targetId = targetId,
                )
            }
        }.toSet()

        val reactionCountMap = reactionCountRepository.getBulk(keys = keys)

        val targetIdReactionMap = accountId?.let {
            val distributionKeyTargetIdMap = targetIds.groupBy { targetId -> XLargeDistributionKey.makeKey(targetId).key }

            return@let distributionKeyTargetIdMap.flatMap { (distributionKey, targetIds) ->
                reactionReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyDistributionKeyAndKeyTargetIdIn(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    accountId = accountId,
                    distributionKey = distributionKey,
                    targetIds = targetIds,
                )
            }.associateBy { it.key.targetId }
        } ?: emptyMap()

        return targetIds.map { targetId ->
            val accountReaction = targetIdReactionMap[targetId]
            ReactionResponse(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                emotions = optionIds.map { optionId ->
                    ReactionEmotionResponse(
                        emotionId = optionId,
                        count = reactionCountMap[
                            ReactionCountKey(
                                workspaceId = workspaceId,
                                componentId = componentId,
                                emotionId = optionId,
                                targetId = targetId,
                            )
                        ] ?: 0L,
                        reactedByMe = accountReaction != null && accountReaction.emotionIds.contains(optionId),
                    )
                }
            )
        }
    }

}
