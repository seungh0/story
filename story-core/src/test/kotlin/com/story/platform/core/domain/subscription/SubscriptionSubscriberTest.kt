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
    private val subscriberCoroutineRepository: SubscriberCoroutineRepository,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscribersCounterCoroutineRepository: SubscribersCounterCoroutineRepository,
    private val subscriptionsCounterCoroutineRepository: SubscriptionsCounterCoroutineRepository,
    private val subscriberDistributedCoroutineRepository: SubscriberDistributedCoroutineRepository,
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
            val alarm = true

            // when
            subscriptionSubscriber.subscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = alarm,
            )

            // then
            val subscriptions = subscriberCoroutineRepository.findAll().toList()
            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarm shouldBe alarm
            }

            val subscriptionReverses = subscriptionCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarm shouldBe alarm
            }

            val subscriptionCounters = subscribersCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 1L
            }

            val subscriberCounters = subscriptionsCounterCoroutineRepository.findAll().toList()
            subscriberCounters shouldHaveSize 1
            subscriberCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.count shouldBe 1L
            }

            val subscriptionDistributed = subscriberDistributedCoroutineRepository.findAll().toList()
            subscriptionDistributed shouldHaveSize 1
            subscriptionDistributed[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.distributedKey shouldBe SubscriberDistributedKeyGenerator.generate(subscriberId)
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
                it.alarm shouldBe alarm
            }
        }

        test("기존에 등록된 구독의 알람 설정을 변경한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "10000"
            val subscriberId = "2000"
            val alarm = true

            subscriberCoroutineRepository.save(
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                    alarm = false,
                )
            )

            subscriptionCoroutineRepository.save(
                SubscriptionReverseFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                    alarm = false,
                )
            )

            subscribersCounterCoroutineRepository.increase(
                key = SubscribersCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                )
            )

            subscriptionsCounterCoroutineRepository.increase(
                key = SubscriptionsCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                )
            )

            subscriberDistributedCoroutineRepository.save(
                SubscriptionDistributorFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    subscriberId = subscriberId,
                    alarm = false,
                )
            )

            // when
            subscriptionSubscriber.subscribe(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = alarm,
            )

            // then
            val subscribers: List<Subscriber> = subscriberCoroutineRepository.findAll().toList()

            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarm shouldBe alarm
            }

            val subscriptionReverses = subscriptionCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarm shouldBe alarm
            }

            val subscriptionCounters = subscribersCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 1L
            }

            val subscriberCounters = subscriptionsCounterCoroutineRepository.findAll().toList()
            subscriberCounters shouldHaveSize 1
            subscriberCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.count shouldBe 1L
            }

            val subscriptionDistributed = subscriberDistributedCoroutineRepository.findAll().toList()
            subscriptionDistributed shouldHaveSize 1
            subscriptionDistributed[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.distributedKey shouldBe SubscriberDistributedKeyGenerator.generate(subscriberId)
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
                it.alarm shouldBe alarm
            }
        }

        test("구독 등록시, 이미 구독한 대상인 경우, 멱등성을 보장한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "10000"
            val subscriberId = "2000"
            val alarm = true

            subscriberCoroutineRepository.save(
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            subscriptionCoroutineRepository.save(
                SubscriptionReverseFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            subscribersCounterCoroutineRepository.increase(
                key = SubscribersCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                )
            )

            subscriptionsCounterCoroutineRepository.increase(
                key = SubscriptionsCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                )
            )

            subscriberDistributedCoroutineRepository.save(
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
                alarm = alarm,
            )

            // then
            val subscribers: List<Subscriber> = subscriberCoroutineRepository.findAll().toList()

            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarm shouldBe alarm
            }

            val subscriptionReverses = subscriptionCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarm shouldBe alarm
            }

            val subscriptionCounters = subscribersCounterCoroutineRepository.findAll().toList()
            subscriptionCounters shouldHaveSize 1
            subscriptionCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.targetId shouldBe targetId
                it.count shouldBe 1L
            }

            val subscriberCounters = subscriptionsCounterCoroutineRepository.findAll().toList()
            subscriberCounters shouldHaveSize 1
            subscriberCounters[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.count shouldBe 1L
            }

            val subscriptionDistributed = subscriberDistributedCoroutineRepository.findAll().toList()
            subscriptionDistributed shouldHaveSize 1
            subscriptionDistributed[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.distributedKey shouldBe SubscriberDistributedKeyGenerator.generate(subscriberId)
                it.key.targetId shouldBe targetId
                it.key.subscriberId shouldBe subscriberId
                it.alarm shouldBe alarm
            }
        }

        test("구독 등록시, 기존에 구독 취소 이력이 있다면, 기존 구독 정보가 저장되었던 동일한 슬롯에 추가한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val targetId = "10000"
            val subscriberId = "2000"
            val alarm = true

            subscriberCoroutineRepository.save(
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            subscriptionCoroutineRepository.save(
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
                alarm = alarm,
            )

            // then
            val subscribers: List<Subscriber> = subscriberCoroutineRepository.findAll().toList()

            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarm shouldBe alarm
            }

            val subscriptionReverses = subscriptionCoroutineRepository.findAll().toList()
            subscriptionReverses shouldHaveSize 1
            subscriptionReverses[0].also {
                it.key.serviceType shouldBe serviceType
                it.key.subscriptionType shouldBe subscriptionType
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarm shouldBe alarm
            }

            val subscriptionCounters = subscribersCounterCoroutineRepository.findAll().toList()
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
