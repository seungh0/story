package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@IntegrationTest
class SubscriptionCountRetrieverTest(
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
    private val subscribersCountRepository: SubscribersCountRepository,
) : FunSpec({

    afterEach {
        subscribersCountRepository.delete(
            key = SubscriberCountKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )
        )
    }

    context("대상자의 구독자 수를 조회한다") {
        test("대상자의 구독자 수를 조회한다") {
            // given
            val count = 999L

            subscribersCountRepository.increase(
                key = SubscriberCountKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                ),
                count = count,
            )

            // when
            val subscribersCount = subscriptionCountRetriever.countSubscribers(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )

            // then
            subscribersCount shouldBe count
        }

        test("대상자의 구독자가 없는 경우 구독자 수가 0명으로 표기된다") {
            // when
            val subscribersCount = subscriptionCountRetriever.countSubscribers(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )

            // then
            subscribersCount shouldBe 0L
        }

        test("대상자가 없는 경우 구독자 수가 0으로 표기된다") {
            // when
            val subscribersCount = subscriptionCountRetriever.countSubscribers(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )

            // then
            subscribersCount shouldBe 0L
        }
    }

}) {

    companion object {
        private val serviceType = ServiceType.TWEETER
        private val subscriptionType = SubscriptionType.FOLLOW
        private const val targetId = "구독 대상자"
    }

}
