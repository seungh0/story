package com.story.core.domain.subscription

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SubscriptionCountRetrieverTest : FunSpec({

    val subscriptionCountRepository = SubscriptionCountMemoryRepository()
    val subscriberCountRepository = SubscriberCountMemoryRepository()

    val subscriptionCountRetriever = SubscriptionCountRetriever(
        subscriptionCountRepository = subscriptionCountRepository,
        subscriberCountRepository = subscriberCountRepository,
    )

    afterEach {
        subscriberCountRepository.clear()
        subscriptionCountRepository.clear()
    }

    context("대상자의 구독자 수를 조회한다") {
        test("특정 대상을 구독한 구독자 수를 조회한다") {
            // given
            val workspaceId = "story"
            val componentId = "follow"
            val targetId = "구독 대상자"
            val count = 999L

            subscriberCountRepository.increase(
                key = SubscriberCountPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                ),
                count = count,
            )

            // when
            val subscriberCount = subscriptionCountRetriever.countSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )

            // then
            subscriberCount shouldBe count
        }

        test("대상자의 구독자가 없는 경우 구독자 수가 0명으로 표기된다") {
            // given
            val workspaceId = "story"
            val componentId = "follow"
            val targetId = "구독 대상자"

            // when
            val subscriberCount = subscriptionCountRetriever.countSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )

            // then
            subscriberCount shouldBe 0L
        }
    }

})
