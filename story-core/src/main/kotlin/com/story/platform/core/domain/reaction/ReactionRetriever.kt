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
        spaceIds: Set<String>,
        accountId: String?,
        optionIds: Set<String>,
    ): List<ReactionResponse> {
        val keys = spaceIds.flatMap { spaceId ->
            optionIds.map { optionId ->
                ReactionCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    emotionId = optionId,
                    spaceId = spaceId,
                )
            }
        }.toSet()

        val reactionCountMap = reactionCountRepository.getBulk(keys = keys)

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
                emotions = optionIds.map { emotionId ->
                    ReactionEmotionResponse(
                        emotionId = emotionId,
                        count = reactionCountMap[
                            ReactionCountKey(
                                workspaceId = workspaceId,
                                componentId = componentId,
                                emotionId = emotionId,
                                spaceId = spaceId,
                            )
                        ] ?: 0L,
                        reactedByMe = accountReaction != null && accountReaction.emotionIds.contains(emotionId),
                    )
                }
            )
        }
    }

}
