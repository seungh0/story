package com.story.platform.core.domain.reaction

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ReactionCreatorTest(
    private val reactionCreator: ReactionCreator,
    private val reactionRepository: ReactionRepository,
    private val reactiveReverseRepository: ReactionReverseRepository,
    private val reactionCountRepository: ReactionCountRepository,
    private val testCleaner: TestCleaner,
) : StringSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    "리액션을 등록한다" {
        // given
        val workspaceId = "workspaceId"
        val componentId = "sticker"
        val targetId = "post-1"
        val accountId = "accountId"
        val optionIds = setOf("1", "2")

        // when
        reactionCreator.upsert(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            accountId = accountId,
            emotionIds = optionIds,
        )

        // then
        val reactions = reactionRepository.findAll().toList()
        reactions shouldHaveSize 1
        reactions[0].also {
            it.key.workspaceId shouldBe workspaceId
            it.key.componentId shouldBe componentId
            it.key.targetId shouldBe targetId
            it.key.accountId shouldBe accountId
            it.key.distributionKey shouldBe XLargeDistributionKey.makeKey(accountId).key
            it.emotionIds shouldBe optionIds
        }

        val reactionReverses = reactiveReverseRepository.findAll().toList()
        reactionReverses shouldHaveSize 1
        reactionReverses[0].also {
            it.key.workspaceId shouldBe workspaceId
            it.key.componentId shouldBe componentId
            it.key.targetId shouldBe targetId
            it.key.accountId shouldBe accountId
            it.key.distributionKey shouldBe XLargeDistributionKey.makeKey(targetId).key
            it.emotionIds shouldBe optionIds
        }

        optionIds.forEach { optionId ->
            val key = ReactionCountKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                emotionId = optionId,
            )
            reactionCountRepository.get(key = key) shouldBe 1L
        }
    }

})
