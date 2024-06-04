package com.story.core.domain.reaction

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class ReactionReaderTest : StringSpec({

    val reactionRepository = mockk<ReactionRepository>()
    val reactionCountRepository = mockk<ReactionCountRepository>()
    val reactionReader = ReactionReader(
        reactionRepository = reactionRepository,
        reactionCountRepository = reactionCountRepository,
    )

    "대상에 등록된 리액션 목록을 조회합니다" {
        // given
        val workspaceId = "workspaceId"
        val componentId = "sticker"
        val spaceId = "post-1"
        val userId = "user-id"

        coEvery {
            reactionCountRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            )
        } returns mapOf(
            ReactionCountKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = "like"
            ) to 10,
            ReactionCountKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = "dislike"
            ) to 20
        )

        coEvery {
            reactionRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyUserIdAndKeyDistributionKeyAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                userId = userId,
                distributionKey = ReactionDistributionKey.makeKey(spaceId),
                spaceId = spaceId,
            )
        } returns ReactionFixture.create(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            emotionIds = setOf("like")
        )

        // when
        val sut = reactionReader.getReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            requestUserId = userId,
        )

        // then
        sut.emotions shouldHaveSize 2
        sut.emotions[0].also {
            it.emotionId shouldBe "like"
            it.count shouldBe 10
            it.reactedByMe shouldBe true
        }
        sut.emotions[1].also {
            it.emotionId shouldBe "dislike"
            it.count shouldBe 20
            it.reactedByMe shouldBe false
        }
    }

})
