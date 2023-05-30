package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.helper.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class SubscriptionRetrieverTest(
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriberRepository: SubscriberRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscribersCounterRepository: SubscribersCounterRepository,
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

            val subscription = SubscriberFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = Subscription.of(subscription)
            subscriberRepository.save(subscription)
            subscriptionRepository.save(subscriptionReverse)

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

            val subscriptionReverse = SubscriptionFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
                status = SubscriptionStatus.DELETED,
            )
            subscriptionRepository.save(subscriptionReverse)

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

            val subscription = SubscriberFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = Subscription.of(subscription)
            subscriberRepository.save(subscription)
            subscriptionRepository.save(subscriptionReverse)

            subscribersCounterRepository.increase(
                key = SubscribersCounterPrimaryKey(
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

            val subscription = SubscriberFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = Subscription.of(subscription)
            subscriberRepository.save(subscription)
            subscriptionRepository.save(subscriptionReverse)

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

    context("구독 대상자를 조회한다") {
        test("첫 페이지를 조회할때 이후에 구독 정보가 더 있는 경우 다음 커서가 반환된다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3)

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 페이지를 조회할때 이후에 구독 정보가 더 없는 경우 다음 커서를 반환하지 않는다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..3).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..3).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("특정 커서 이후의 구독 정보를 조회할때 뒤에 구독 정보가 더 있는 경우 다음 커서가 반환된다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..3).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..3).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "1", direction = CursorDirection.NEXT, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3)

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("특정 커서 이후의 구독 정보를 조회할때 뒤에 구독 정보가 더 없는 경우 다음 커서가 반환되지 않는다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "1", direction = CursorDirection.NEXT, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3)

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("마지막 페이지를 조회한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(2, 4)

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("특정 커서 이전의 구독 정보를 조회할때 이전의 구독 정보가 더 있는 경우 이전 커서가 반환된다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "4", direction = CursorDirection.PREVIOUS, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3)

            sut.cursor.nextCursor shouldBe "2"
            sut.cursor.hasNext shouldBe true
        }

        test("특정 커서 이전의 구독 정보를 조회할때 이전의 구독 정보가 더 없는 경우 이전 커서가 반환되지 않는다") {
            // given
            val serviceType = ServiceType.TWEETER
            val subscriptionType = SubscriptionType.FOLLOW
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "4", direction = CursorDirection.PREVIOUS, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3)

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }
    }

})
