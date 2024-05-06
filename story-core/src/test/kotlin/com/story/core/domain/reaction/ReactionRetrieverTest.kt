package com.story.core.domain.reaction

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@IntegrationTest
class ReactionRetrieverTest(
    private val reactionRetriever: ReactionRetriever,
    private val reactionReverseRepository: ReactionReverseRepository,
    private val reactionCountRepository: ReactionCountRepository,
) : StringSpecIntegrationTest({

    "리액션을 조회합니다" {
        // given
        val workspaceId = "story"
        val componentId = "like"
        val spaceId = "comment-1"
        val emotionId1 = "like"
        val emotionId2 = "dislike"

        val reaction = ReactionFixture.create(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            optionIds = setOf(emotionId1)
        )
        reactionReverseRepository.save(ReactionReverse.from(reaction))

        reactionCountRepository.increase(
            key = ReactionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = emotionId1,
            ),
            count = 5L,
        )

        reactionCountRepository.increase(
            key = ReactionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = emotionId2,
            ),
            count = 3L,
        )

        // when
        val sut = reactionRetriever.getReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            requestUserId = reaction.key.userId,
        )

        // then
        sut.workspaceId shouldBe workspaceId
        sut.componentId shouldBe componentId
        sut.spaceId shouldBe spaceId
        sut.emotions shouldHaveSize 2
        sut.emotions[0].also {
            it.emotionId shouldBe emotionId1
            it.count shouldBe 5L
            it.reactedByMe shouldBe true
        }
        sut.emotions[1].also {
            it.emotionId shouldBe emotionId2
            it.count shouldBe 3L
            it.reactedByMe shouldBe false
        }
    }

    "리액션 목록을 조회합니다" {
        // given
        val workspaceId = "story"
        val componentId = "like"
        val spaceId1 = "comment-1"
        val spaceId2 = "comment-2"
        val emotionId1 = "like"
        val emotionId2 = "dislike"

        val reaction1 = ReactionFixture.create(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId1,
            optionIds = setOf(emotionId1)
        )
        reactionReverseRepository.save(ReactionReverse.from(reaction1))

        reactionCountRepository.increase(
            key = ReactionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId1,
                emotionId = emotionId1,
            ),
            count = 5L,
        )

        reactionCountRepository.increase(
            key = ReactionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId1,
                emotionId = emotionId2,
            ),
            count = 3L,
        )

        reactionCountRepository.increase(
            key = ReactionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId2,
                emotionId = emotionId1,
            ),
            count = 10L,
        )

        // when
        val sut = reactionRetriever.listReactions(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceIds = setOf(spaceId1, spaceId2),
            requestUserId = reaction1.key.userId,
        )

        // then
        sut shouldHaveSize 2
        sut[0].also {
            it.workspaceId shouldBe workspaceId
            it.componentId shouldBe componentId
            it.spaceId shouldBe spaceId1
            it.emotions shouldHaveSize 2
            it.emotions[0].also { emotion ->
                emotion.emotionId shouldBe emotionId1
                emotion.count shouldBe 5L
                emotion.reactedByMe shouldBe true
            }
            it.emotions[1].also { emotion ->
                emotion.emotionId shouldBe emotionId2
                emotion.count shouldBe 3L
                emotion.reactedByMe shouldBe false
            }
        }
        sut[1].also {
            it.workspaceId shouldBe workspaceId
            it.componentId shouldBe componentId
            it.spaceId shouldBe spaceId2
            it.emotions shouldHaveSize 1
            it.emotions[0].also { emotion ->
                emotion.emotionId shouldBe emotionId1
                emotion.count shouldBe 10L
                emotion.reactedByMe shouldBe false
            }
        }
    }

})
