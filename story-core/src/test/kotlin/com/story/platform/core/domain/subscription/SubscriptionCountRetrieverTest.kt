package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.helper.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@IntegrationTest
class SubscriptionCountRetrieverTest(
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
    private val subscribersCounterRepository: SubscribersCounterRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("대상자의 구독자 수를 조회한다") {
        test("대상자의 구독자 수를 조회한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"
            val count = 999L

            subscribersCounterRepository.increase(
                key = SubscribersCounterPrimaryKey(
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
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"

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
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"

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

})
