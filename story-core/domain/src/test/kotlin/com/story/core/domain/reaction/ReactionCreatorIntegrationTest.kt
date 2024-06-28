package com.story.core.domain.reaction

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ReactionCreatorIntegrationTest(
    private val reactionCreator: ReactionCreator,
    private val reactionCassandraRepository: ReactionCassandraRepository,
    private val reactiveReverseRepository: ReactionReverseCassandraRepository,
) : StringSpecIntegrationTest({

    "리액션을 등록한다" {
        // given
        val workspaceId = "workspaceId"
        val componentId = "sticker"
        val spaceId = "post-1"
        val userId = "user-id"
        val optionIds = setOf("1", "2")

        // when
        reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
            emotionIds = optionIds,
        )

        // then
        val reactions = reactionCassandraRepository.findAll().toList()
        reactions shouldHaveSize 1
        reactions[0].also {
            it.key.workspaceId shouldBe workspaceId
            it.key.componentId shouldBe componentId
            it.key.spaceId shouldBe spaceId
            it.key.userId shouldBe userId
            it.key.distributionKey shouldBe ReactionDistributionKey.makeKey(userId)
            it.emotionIds shouldBe optionIds
        }

        val reactionReverses = reactiveReverseRepository.findAll().toList()
        reactionReverses shouldHaveSize 1
        reactionReverses[0].also {
            it.key.workspaceId shouldBe workspaceId
            it.key.componentId shouldBe componentId
            it.key.spaceId shouldBe spaceId
            it.key.userId shouldBe userId
            it.key.distributionKey shouldBe ReactionDistributionKey.makeKey(spaceId)
            it.emotionIds shouldBe optionIds
        }
    }

})
