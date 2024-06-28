package com.story.core.domain.reaction

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ReactionRemoverTest(
    private val reactionRemover: ReactionRemover,
    private val reactionCassandraRepository: ReactionCassandraRepository,
    private val reactionReverseCassandraRepository: ReactionReverseCassandraRepository,
) : StringSpecIntegrationTest({

    "리액션을 취소합니다" {
        // given
        val workspaceId = "story"
        val componentId = "like"
        val spaceId = "comment-1"
        val emotionId = "like"

        val reaction = ReactionEntityFixture.create(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            optionIds = setOf(emotionId)
        )
        reactionCassandraRepository.save(reaction)
        reactionReverseCassandraRepository.save(ReactionReverseEntity.from(reaction))

        // when
        reactionRemover.removeReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = reaction.key.userId,
        )

        // then
        val reactions = reactionCassandraRepository.findAll().toList()
        reactions shouldHaveSize 0

        val reactionReverses = reactionReverseCassandraRepository.findAll().toList()
        reactionReverses shouldHaveSize 0
    }

})
