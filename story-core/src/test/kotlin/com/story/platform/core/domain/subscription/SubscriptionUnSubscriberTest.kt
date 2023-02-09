package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class SubscriptionUnSubscriberTest(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
) : FunSpec({

    afterEach {
        subscriptionCoroutineRepository.deleteAll()
        subscriptionReverseCoroutineRepository.deleteAll()
        subscriptionCounterCoroutineRepository.deleteAll()
    }

    context("구독을 취소한다") {
        test("기존의 구독 정보를 취소한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = "follow"
            val targetId = "10000"
            val subscriberId = "2000"

            subscriptionCoroutineRepository.save(
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            subscriptionReverseCoroutineRepository.save(
                SubscriptionReverseFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            subscriptionCounterCoroutineRepository.increase(
                key = SubscriptionCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                )
            )

            // when
            subscriptionUnSubscriber.unsubscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            val subscriptions = subscriptionCoroutineRepository.findAll().toList()
            subscriptions shouldHaveSize 0

            val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.DELETED
            }

            val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 0L
            }
        }

        test("구독 정보가 없을 때 구독 정보를 취소하더라도 멱등성을 보장한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = "follow"
            val targetId = "10000"
            val subscriberId = "2000"

            // when
            subscriptionUnSubscriber.unsubscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()
            subscriptions shouldHaveSize 0

            val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 0

            val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 0
        }

        test("구독 취소시 이미 취소된 이력이 있다면 유지된다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = "follow"
            val targetId = "10000"
            val subscriberId = "2000"

            subscriptionReverseCoroutineRepository.save(
                SubscriptionReverseFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                    status = SubscriptionStatus.DELETED,
                )
            )

            // when
            subscriptionUnSubscriber.unsubscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            val subscriptions = subscriptionCoroutineRepository.findAll().toList()
            subscriptions shouldHaveSize 0

            val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.DELETED
            }

            val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 0
        }
    }

})
