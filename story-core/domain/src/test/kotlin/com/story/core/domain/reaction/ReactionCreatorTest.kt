package com.story.core.domain.reaction

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class ReactionCreatorTest : StringSpec({

    val reactionRepository = mockk<ReactionRepository>(relaxed = true)
    val reactionCreator = ReactionCreator(
        reactionRepository = reactionRepository,
    )

    "리액션을 신규로 등록합니다" {
        // given
        val workspaceId = "story"
        val componentId = "like"
        val spaceId = "post-1"
        val userId = "user-1"
        val emotionIds = setOf("like", "dislike")

        coEvery { reactionRepository.findById(workspaceId, componentId, spaceId, userId) } returns null

        val sut = reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
            emotionIds = emotionIds
        )

        // then
        sut.createdOptionIds shouldBe emotionIds
        sut.deletedOptionIds shouldBe emptySet()

        coVerify(exactly = 1) { reactionRepository.create(workspaceId, componentId, spaceId, userId, emotionIds) }
    }

    "기존에 등록되어 있던 리액션을 신규 리액션으로 변경합니다" {
        // given
        val workspaceId = "story"
        val componentId = "like"
        val spaceId = "post-1"
        val userId = "user-1"
        val emotionIds = setOf("like", "dislike")

        val previousReaction = ReactionFixture.create()
        coEvery { reactionRepository.findById(workspaceId, componentId, spaceId, userId) } returns previousReaction

        val sut = reactionCreator.upsertReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            userId = userId,
            emotionIds = emotionIds
        )

        // then
        sut.createdOptionIds shouldBe emotionIds
        sut.deletedOptionIds shouldBe previousReaction.emotionIds

        coVerify(exactly = 1) { reactionRepository.create(workspaceId, componentId, spaceId, userId, emotionIds) }
    }

})
