package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@IntegrationTest
class SubscriptionCountRetrieverTest(
    private val subscriberCountRepository: SubscriberCountRepository,
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("대상자의 구독자 수를 조회한다") {
        test("특정 대상을 구독한 구독자 수를 조회한다") {
            // given
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
            val subscribersCount = subscriptionCountRetriever.countSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )

            // then
            subscribersCount shouldBe count
        }

        test("대상자의 구독자가 없는 경우 구독자 수가 0명으로 표기된다") {
            // when
            val subscribersCount = subscriptionCountRetriever.countSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )

            // then
            subscribersCount shouldBe 0L
        }
    }

}) {

    companion object {
        private const val workspaceId = "twitter"
        private const val componentId = "follow"
        private const val targetId = "구독 대상자"
    }

}
