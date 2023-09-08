package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class SubscriptionRetrieverTest(
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriberRepository: SubscriberRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberSequenceRepository: SubscriberSequenceRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("특정 계정이 대상을 구독하였는지 확인한다") {
        test("대상자를 구독한 기록이 있으면, 구독자로 판단한다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "구독 대상자"
            val subscriberId = "구독자"

            val subscriber = SubscriberFixture.create(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
            )
            val subscription = Subscription.of(subscriber)
            subscriberRepository.save(subscriber)
            subscriptionRepository.save(subscription)

            // when
            val isSubscriber = subscriptionRetriever.isSubscriber(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            isSubscriber shouldBe true
        }

        test("대상자를 구독한 기록이 없다면, 구독자가 아니라고 판단한다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "구독 대상자"
            val subscriberId = "구독자"

            // when
            val isSubscriber = subscriptionRetriever.isSubscriber(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            isSubscriber shouldBe false
        }

        test("구독이 취소되어 있는 경우, 구독자가 아니라고 판단한다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "구독 대상자"
            val subscriberId = "구독자"

            val subscription = SubscriptionFixture.create(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = 1L,
                subscriberId = subscriberId,
                status = SubscriptionStatus.DELETED,
            )
            subscriptionRepository.save(subscription)

            // when
            val isSubscriber = subscriptionRetriever.isSubscriber(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            // then
            isSubscriber shouldBe false
        }
    }

    context("특정 대상을 구독한 구독자 목록을 조회합니다 - NEXT 방향 + 첫 커서") {
        test("첫 슬롯(0개) - 처음부터 1개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 1),
            )

            // then
            sut.data shouldHaveSize 0

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(0개) ~ 두번째 슬롯(3개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 2L

            val subscribers = (1..3).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..3).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 3L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(4개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 1L

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = 4L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(4개) - 처음부터 4개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 1L

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = 4L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 4),
            )

            // then
            sut.data shouldHaveSize 4
            sut.data shouldBe subscriptions.subList(0, 4).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(3개) ~ 두번째 슬롯(1개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 1L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(3개) ~ 두번째 슬롯(1개) - 처음부터 4개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 1L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 4),
            )

            // then
            sut.data shouldHaveSize 4
            sut.data shouldBe subscriptions.subList(0, 4).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(2개) ~ 두번째 슬롯(2개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 2L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }
    }

    context("특정 대상을 구독한 구독자 목록을 조회합니다 - NEXT 방향 + 처음이 아닌 커서") {
        test("첫 슬롯(4개) - 특정 키부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 1L

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = 4L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(
                    cursor = subscribers[0].key.subscriberId,
                    direction = CursorDirection.NEXT,
                    pageSize = 3
                ),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(1, 4).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(2개) ~ 두번째 슬롯(2개) - 특정 키부터 2개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 2L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(
                    cursor = subscribers[0].key.subscriberId,
                    direction = CursorDirection.NEXT,
                    pageSize = 2,
                ),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(2개) ~ 두번째 슬롯(2개) - 특정 키부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 2L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(
                    cursor = subscribers[0].key.subscriberId,
                    direction = CursorDirection.NEXT,
                    pageSize = 3,
                ),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(1, 4).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }
    }

    context("특정 대상을 구독한 구독자 목록을 조회합니다 - PREVIOUS 방향 + 첫 커서") {
        test("첫 슬롯(0개) - 처음부터 1개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 1),
            )

            // then
            sut.data shouldHaveSize 0

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(0개) ~ 두번째 슬롯(3개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 2L

            val subscribers = (1..3).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..3).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 3L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.reversed().subList(0, 3)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "1" // 커서가 반환되지만 이전 슬롯에 없음
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(4개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 1L

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = 4L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.reversed().subList(0, 3)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "2"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(4개) - 처음부터 4개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 1L

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = 4L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 4),
            )

            // then
            sut.data shouldHaveSize 4
            sut.data shouldBe subscriptions.reversed().subList(0, 4)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(3개) ~ 두번째 슬롯(1개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 1L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.reversed().subList(0, 3)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "2"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(3개) ~ 두번째 슬롯(1개) - 처음부터 4개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 4,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 1L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 4),
            )

            // then
            sut.data shouldHaveSize 4
            sut.data shouldBe subscriptions.reversed().subList(0, 4)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(2개) ~ 두번째 슬롯(2개) - 처음부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 2L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.reversed().subList(0, 3)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "2"
            sut.cursor.hasNext shouldBe true
        }
    }

    context("특정 대상을 구독한 구독자 목록을 조회합니다 - PREVIOUS 방향 + 처음이 아닌 커서") {
        test("첫 슬롯(4개) - 특정 키부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"
            val slotId = 1L

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = slotId,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = 4L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(
                    cursor = subscribers.last().key.subscriberId,
                    direction = CursorDirection.PREVIOUS,
                    pageSize = 3
                ),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.reversed().subList(1, 4)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("첫 슬롯(2개) ~ 두번째 슬롯(2개) - 특정 키부터 2개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 2L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(
                    cursor = subscribers.last().key.subscriberId,
                    direction = CursorDirection.PREVIOUS,
                    pageSize = 2,
                ),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.reversed().subList(1, 3)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "2"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 슬롯(2개) ~ 두번째 슬롯(2개) - 특정 키부터 3개 조회") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val targetId = "targetId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = it.toString(),
                    targetId = targetId,
                    slotId = SubscriptionSlotAssigner.FIRST_SLOT_ID + it.toLong() / 3,
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            subscriberSequenceRepository.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                count = SubscriptionSlotAssigner.SLOT_SIZE + 2L,
            )

            // when
            val sut = subscriptionRetriever.listTargetSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = CursorRequest(
                    cursor = subscribers.last().key.subscriberId,
                    direction = CursorDirection.PREVIOUS,
                    pageSize = 3,
                ),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.reversed().subList(1, 4)
                .map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }
    }

    context("구독자가 구독한 대상자 목록을 조회합니다") {
        test("첫 페이지를 조회할때 이후에 구독 정보가 더 있는 경우 다음 커서가 반환된다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("첫 페이지를 조회할때 이후에 구독 정보가 더 없는 경우 다음 커서를 반환하지 않는다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..3).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..3).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.NEXT, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("특정 커서 이후의 구독 정보를 조회할때 뒤에 구독 정보가 더 있는 경우 다음 커서가 반환된다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..3).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..3).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "1", direction = CursorDirection.NEXT, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }

        test("특정 커서 이후의 구독 정보를 조회할때 뒤에 구독 정보가 더 없는 경우 다음 커서가 반환되지 않는다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "1", direction = CursorDirection.NEXT, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("마지막 페이지를 조회한다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = null, direction = CursorDirection.PREVIOUS, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(2, 4).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "3"
            sut.cursor.hasNext shouldBe true
        }

        test("특정 커서 이전의 구독 정보를 조회할때 이전의 구독 정보가 더 있는 경우 이전 커서가 반환된다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "4", direction = CursorDirection.PREVIOUS, pageSize = 2),
            )

            // then
            sut.data shouldHaveSize 2
            sut.data shouldBe subscriptions.subList(1, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe "2"
            sut.cursor.hasNext shouldBe true
        }

        test("특정 커서 이전의 구독 정보를 조회할때 이전의 구독 정보가 더 없는 경우 이전 커서가 반환되지 않는다") {
            // given
            val workspaceId = "twitter"
            val componentId = "follow"
            val subscriberId = "subscriberId"

            val subscribers = (1..4).map {
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriberRepository.saveAll(subscribers).toList()

            val subscriptions = (1..4).map {
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = it.toString(),
                    alarm = true,
                )
            }
            subscriptionRepository.saveAll(subscriptions).toList()

            // when
            val sut = subscriptionRetriever.listSubscriberTargets(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = CursorRequest(cursor = "4", direction = CursorDirection.PREVIOUS, pageSize = 3),
            )

            // then
            sut.data shouldHaveSize 3
            sut.data shouldBe subscriptions.subList(0, 3).map { subscription -> SubscriptionResponse.of(subscription) }

            sut.cursor.nextCursor shouldBe null
            sut.cursor.hasNext shouldBe false
        }
    }

})
