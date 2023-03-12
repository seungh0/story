package com.story.platform.core.domain.subscription

import com.story.platform.core.common.distribution.LargeDistributionKey
import com.story.platform.core.common.enums.ServiceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class SubscriptionSubscriberTest(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val subscriptionDistributedCoroutineRepository: SubscriptionDistributedCoroutineRepository,
) : FunSpec({

    afterEach {
        subscriptionCoroutineRepository.deleteAll()
        subscriptionReverseCoroutineRepository.deleteAll()
        subscriptionCounterCoroutineRepository.deleteAll()
        subscriptionDistributedCoroutineRepository.deleteAll()
    }

    context("구독을 추가한다") {
        test("구독 정보를 추가합니다") {
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
                it.key.distributedKey shouldBe LargeDistributionKey.fromId(subscriberId).key
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
            }
        }

        test("구독 정보를 추가할때 이미 구독 정보가 있는 경우라도 멱등성을 보장한다") {
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
                SubscriptionDistributedFixture.create(
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
                it.key.distributedKey shouldBe LargeDistributionKey.fromId(subscriberId).key
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
            }
        }

        test("구독 정보를 추가할때 구독 취소한 이력이 있다면 기존 슬롯에 추가한다") {
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
