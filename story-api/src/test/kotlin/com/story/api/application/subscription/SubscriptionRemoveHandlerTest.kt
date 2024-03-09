package com.story.api.application.subscription

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscriptionCountManager
import com.story.core.domain.subscription.SubscriptionEventProducer
import com.story.core.domain.subscription.SubscriptionUnSubscriber
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SubscriptionRemoveHandlerTest : StringSpec({

    val subscriptionUnSubscriber = mockk<SubscriptionUnSubscriber>()
    val subscriptionCountManager = mockk<SubscriptionCountManager>(relaxed = true)
    val subscriptionEventProducer = mockk<SubscriptionEventProducer>(relaxed = true)
    val componentCheckHandler = mockk<ComponentCheckHandler>(relaxed = true)

    val handler = SubscriptionRemoveHandler(
        subscriptionUnSubscriber = subscriptionUnSubscriber,
        subscriptionCountManager = subscriptionCountManager,
        subscriptionEventProducer = subscriptionEventProducer,
        componentCheckHandler = componentCheckHandler,
    )

    "구독 취소시 구독 컴포넌트가 존재하는지 검증한다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"
        val targetId = "fb4fc361-3695-46b5-879e-e7ccdec912bb"
        val subscriberId = "37568eea-1eb5-481c-9f38-51fb77b609e7"

        coEvery {
            subscriptionUnSubscriber.removeSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns true

        // when
        handler.removeSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        // then
        coVerify(exactly = 1) {
            componentCheckHandler.checkExistsComponent(
                workspaceId = workspaceId,
                componentId = componentId,
                resourceId = ResourceId.SUBSCRIPTIONS
            )
        }
    }

    "등록되어 있던 구독이 취소되는 경우 구독 카운트를 감소한다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"
        val targetId = "fb4fc361-3695-46b5-879e-e7ccdec912bb"
        val subscriberId = "37568eea-1eb5-481c-9f38-51fb77b609e7"

        coEvery {
            subscriptionUnSubscriber.removeSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns true

        // when
        handler.removeSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        // then
        coVerify(exactly = 1) {
            subscriptionCountManager.decrease(
                workspaceId = any(),
                componentId = any(),
                subscriberId = any(),
                targetId = any(),
            )
        }
        coVerify(exactly = 1) {
            subscriptionCountManager.decrease(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

    "등록되지 않았던 구독인 경우 구독 카운트를 감소하지 않는다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"
        val targetId = "fb4fc361-3695-46b5-879e-e7ccdec912bb"
        val subscriberId = "37568eea-1eb5-481c-9f38-51fb77b609e7"

        coEvery {
            subscriptionUnSubscriber.removeSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns false

        // when
        handler.removeSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        // then
        coVerify(exactly = 0) {
            subscriptionCountManager.decrease(
                workspaceId = any(),
                componentId = any(),
                subscriberId = any(),
                targetId = any(),
            )
        }
    }

    "등록되어 있던 구독이 취소되는 경우 구독 취소 이벤트를 발행한다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"
        val targetId = "fb4fc361-3695-46b5-879e-e7ccdec912bb"
        val subscriberId = "37568eea-1eb5-481c-9f38-51fb77b609e7"

        coEvery {
            subscriptionUnSubscriber.removeSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns true

        // when
        handler.removeSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        // then
        coVerify(exactly = 1) {
            subscriptionEventProducer.publishRemovedEvent(
                workspaceId = any(),
                componentId = any(),
                subscriberId = any(),
                targetId = any(),
            )
        }
        coVerify(exactly = 1) {
            subscriptionEventProducer.publishRemovedEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

    "등록되지 않았던 구독인 경우 구독 취소 이벤트를 발행하지 않는다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"
        val targetId = "fb4fc361-3695-46b5-879e-e7ccdec912bb"
        val subscriberId = "37568eea-1eb5-481c-9f38-51fb77b609e7"

        coEvery {
            subscriptionUnSubscriber.removeSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns false

        // when
        handler.removeSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )

        // then
        coVerify(exactly = 0) {
            subscriptionEventProducer.publishRemovedEvent(
                workspaceId = any(),
                componentId = any(),
                subscriberId = any(),
                targetId = any(),
            )
        }
    }

})
