package com.story.core.domain.reaction

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class ReactionReader(
    private val reactionCountRepository: ReactionCountRepository,
    private val reactionRepository: ReactionRepository,
) {

    suspend fun getReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        requestUserId: String?,
    ): ReactionWithEmotionCount {
        val reactionCountMap = reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
        )

        val spaceIdReaction = requestUserId?.let {
            reactionRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                userId = requestUserId,
                distributionKey = ReactionDistributionKey.makeKey(spaceId),
                spaceId = spaceId,
            )
        }

        return ReactionWithEmotionCount(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotions = reactionCountMap.asSequence()
                .filter { it.value > 0 }
                .map {
                    ReactionEmotion.of(
                        emotionId = it.key.emotionId,
                        count = reactionCountMap[
                            ReactionCountKey(
                                workspaceId = workspaceId,
                                componentId = componentId,
                                emotionId = it.key.emotionId,
                                spaceId = spaceId,
                            )
                        ] ?: 0L,
                        reactedByMe = spaceIdReaction != null && spaceIdReaction.emotionIds.contains(it.key.emotionId),
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
    ): List<ReactionWithEmotionCount> = coroutineScope {
        val reactionCountMap = spaceIds.map { spaceId ->
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
                    reactionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceIdIn(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        userId = requestUserId,
                        distributionKey = distributionKey,
                        spaceIds = spaceIds,
                    )
                }
            }.awaitAll().flatten().associateBy { it.spaceId }
        } ?: emptyMap()

        // TODO: 버그 있어서 다시 개발
        return@coroutineScope emptyList()

//        return@coroutineScope spaceIds.map { spaceId ->
//            val reactionByRequestUserId = spaceIdReactionMap[spaceId]
//            ReactionWithEmotionCount(
//                workspaceId = workspaceId,
//                componentId = componentId,
//                spaceId = spaceId,
//                emotions = emotionIds.asSequence()
//                    .filter { emotionId ->
//                        (
//                            reactionCountMap[
//                                ReactionCountKey(
//                                    workspaceId = workspaceId,
//                                    componentId = componentId,
//                                    spaceId = spaceId,
//                                    emotionId = emotionId,
//                                )
//                            ]?.count ?: 0L
//                            ) > 0
//                    }
//                    .map { emotionId ->
//                        ReactionEmotion.of(
//                            emotionId = emotionId,
//                            count = reactionCountMap[
//                                ReactionCountKey(
//                                    workspaceId = workspaceId,
//                                    componentId = componentId,
//                                    emotionId = emotionId,
//                                    spaceId = spaceId,
//                                )
//                            ]?.count ?: 0L,
//                            reactedByMe = reactionByRequestUserId != null &&
//                                reactionByRequestUserId.emotionIds.contains(emotionId),
//                        )
//                    }
//                    .toList()
//            )
//        }
    }

}
