package com.story.core.domain.subscription

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class SubscriptionCountManagerTest : FunSpec({

    val subscriberCountRepository = SubscriberCountMemoryRepository()
    val subscriptionCountRepository = SubscriptionCountMemoryRepository()

    val subscriptionCountManager = SubscriptionCountManager(
        subscriberCountRepository = subscriberCountRepository,
        subscriptionCountRepository = subscriptionCountRepository,
    )

    afterEach {
        subscriberCountRepository.clear()
        subscriptionCountRepository.clear()
    }

    test("구독 카운트 증가시 구독자와 구독 대상 카운트가 1 증가한다") {
        // given
        val workspaceId = "workspace-id"
        val componentId = "component-id"
        val targetId = "target-id"
        val subscriberId = "subscriber-id"

        // when
        subscriptionCountManager.increase(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        val subscriptionCounts = subscriptionCountRepository.findAll().toList()
        subscriptionCounts shouldHaveSize 1
        subscriptionCounts.first().also {
            it.first.workspaceId shouldBe workspaceId
            it.first.componentId shouldBe componentId
            it.first.subscriberId shouldBe subscriberId
            it.second shouldBe 1L
        }

        val subscriberCounts = subscriberCountRepository.findAll().toList()
        subscriberCounts shouldHaveSize 1
        subscriberCounts.first().also {
            it.first.workspaceId shouldBe workspaceId
            it.first.componentId shouldBe componentId
            it.first.targetId shouldBe targetId
            it.second shouldBe 1L
        }
    }

    test("구독 카운트 감소시 구독자와 구독 대상 카운트가 1 감소한다") {
        // given
        val workspaceId = "workspace-id"
        val componentId = "component-id"
        val targetId = "target-id"
        val subscriberId = "subscriber-id"

        // when
        subscriptionCountManager.decrease(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        val subscriptionCounts = subscriptionCountRepository.findAll().toList()
        subscriptionCounts shouldHaveSize 1
        subscriptionCounts.first().also {
            it.first.workspaceId shouldBe workspaceId
            it.first.componentId shouldBe componentId
            it.first.subscriberId shouldBe subscriberId
            it.second shouldBe -1L
        }

        val subscriberCounts = subscriberCountRepository.findAll().toList()
        subscriberCounts shouldHaveSize 1
        subscriberCounts.first().also {
            it.first.workspaceId shouldBe workspaceId
            it.first.componentId shouldBe componentId
            it.first.targetId shouldBe targetId
            it.second shouldBe -1L
        }
    }

})
