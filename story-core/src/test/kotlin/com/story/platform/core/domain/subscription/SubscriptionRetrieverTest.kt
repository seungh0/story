package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.helper.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@IntegrationTest
internal class SubscriptionRetrieverTest(
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("유저가 대상에게 구독자인지 확인한다") {
        test("대상자를 구독한 기록이 있으면, 구독자이다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"
            val subscriberId = "구독자"

            val subscription = SubscriptionFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = SubscriptionReverse.of(subscription)
            subscriptionCoroutineRepository.save(subscription)
            subscriptionReverseCoroutineRepository.save(subscriptionReverse)

            // when
            val isSubscriber = subscriptionRetriever.isSubscriber(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            isSubscriber shouldBe true
        }

        test("대상자를 구독한 기록이 없다면, 구독자가 아니다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"
            val subscriberId = "구독자"

            // when
            val isSubscriber = subscriptionRetriever.isSubscriber(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            isSubscriber shouldBe false
        }

        test("구독이 취소되어 있는 경우, 구독자가 아니다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"
            val subscriberId = "구독자"

            val subscriptionReverse = SubscriptionReverseFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
                status = SubscriptionStatus.DELETED,
            )
            subscriptionReverseCoroutineRepository.save(subscriptionReverse)

            // when
            val isSubscriber = subscriptionRetriever.isSubscriber(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            isSubscriber shouldBe false
        }
    }

    context("대상자의 구독자 수를 조회한다") {
        test("대상자의 구독자 수를 조회한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "구독 대상자"
            val subscriberId = "구독자"
            val count = 999L

            val subscription = SubscriptionFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = SubscriptionReverse.of(subscription)
            subscriptionCoroutineRepository.save(subscription)
            subscriptionReverseCoroutineRepository.save(subscriptionReverse)

            subscriptionCounterCoroutineRepository.increase(
                key = SubscriptionCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                ),
                count = count,
            )

            // when
            val subscribersCount = subscriptionRetriever.countSubscribers(
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
            val subscriberId = "구독자"

            val subscription = SubscriptionFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = SubscriptionReverse.of(subscription)
            subscriptionCoroutineRepository.save(subscription)
            subscriptionReverseCoroutineRepository.save(subscriptionReverse)

            // when
            val subscribersCount = subscriptionRetriever.countSubscribers(
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
            val subscribersCount = subscriptionRetriever.countSubscribers(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )

            // then
            subscribersCount shouldBe 0L
        }
    }

})
