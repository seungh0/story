package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.helper.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class SubscriptionSubscriberTest(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val subscriptionDistributedCoroutineRepository: SubscriptionDistributedCoroutineRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("구독을 추가한다") {
        test("새로운 구독 정보를 추가합니다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "10000"
            val subscriberId = "2000"

            // when
            subscriptionSubscriber.subscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            val subscriptions = subscriptionCoroutineRepository.findAll().toList()
            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
            }

            val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
            }

            val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 1L
            }

            val subscriptionDistributed = subscriptionDistributedCoroutineRepository.findAll().toList()
            subscriptionDistributed shouldHaveSize 1
            subscriptionDistributed[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.distributedKey shouldBe SubscriptionDistributedKeyGenerator.generate(subscriberId)
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
            }
        }

        test("구독 등록시, 이미 구독한 대상인 경우, 멱등성을 보장한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
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

            subscriptionDistributedCoroutineRepository.save(
                SubscriptionDistributorFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    subscriberId = subscriberId,
                )
            )

            // when
            subscriptionSubscriber.subscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()

            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
            }

            val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
            }

            val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 1L
            }

            val subscriptionDistributed = subscriptionDistributedCoroutineRepository.findAll().toList()
            subscriptionDistributed shouldHaveSize 1
            subscriptionDistributed[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.distributedKey shouldBe SubscriptionDistributedKeyGenerator.generate(subscriberId)
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
            }
        }

        test("구독 등록시, 기존에 구독 취소 이력이 있다면, 기존 구독 정보가 저장되었던 동일한 슬롯에 추가한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
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
                    status = SubscriptionStatus.DELETED,
                )
            )

            // when
            subscriptionSubscriber.subscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()

            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
            }

            val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
            }

            val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 1L
            }
        }
    }

})
