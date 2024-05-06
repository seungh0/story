package com.story.core.domain.reaction

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ReactionRemoverTest(
    private val reactionRemover: ReactionRemover,
    private val reactionRepository: ReactionRepository,
    private val reactionReverseRepository: ReactionReverseRepository,
) : StringSpecIntegrationTest({

    "리액션을 취소합니다" {
        // given
        val workspaceId = "story"
        val componentId = "like"
        val spaceId = "comment-1"
        val emotionId = "like"

        val reaction = ReactionFixture.create(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            optionIds = setOf(emotionId)
        )
        reactionRepository.save(reaction)
        reactionReverseRepository.save(ReactionReverse.from(reaction))

        // when
        reactionRemover.removeReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = reaction.key.userId,
        )

        // then
        val reactions = reactionRepository.findAll().toList()
        reactions shouldHaveSize 0

        val reactionReverses = reactionReverseRepository.findAll().toList()
        reactionReverses shouldHaveSize 0
    }

})
