package com.story.platform.core.domain.reaction

import com.story.platform.core.IntegrationTest
import com.story.platform.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ReactionCreatorTest(
    private val reactionCreator: ReactionCreator,
    private val reactionRepository: ReactionRepository,
    private val reactiveReverseRepository: ReactionReverseRepository,
) : StringSpecIntegrationTest({

    "리액션을 등록한다" {
        // given
        val workspaceId = "workspaceId"
        val componentId = "sticker"
        val spaceId = "post-1"
        val accountId = "accountId"
        val optionIds = setOf("1", "2")

        // when
        reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = accountId,
            emotionIds = optionIds,
        )

        // then
        val reactions = reactionRepository.findAll().toList()
        reactions shouldHaveSize 1
        reactions[0].also {
            it.key.workspaceId shouldBe workspaceId
            it.key.componentId shouldBe componentId
            it.key.spaceId shouldBe spaceId
            it.key.accountId shouldBe accountId
            it.key.distributionKey shouldBe ReactionDistributionKey.makeKey(accountId)
            it.emotionIds shouldBe optionIds
        }

        val reactionReverses = reactiveReverseRepository.findAll().toList()
        reactionReverses shouldHaveSize 1
        reactionReverses[0].also {
            it.key.workspaceId shouldBe workspaceId
            it.key.componentId shouldBe componentId
            it.key.spaceId shouldBe spaceId
            it.key.accountId shouldBe accountId
            it.key.distributionKey shouldBe ReactionDistributionKey.makeKey(spaceId)
            it.emotionIds shouldBe optionIds
        }
    }

})
