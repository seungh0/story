package com.story.api.application.subscription

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscribeSelfForbiddenException
import com.story.core.domain.subscription.SubscriptionCountManager
import com.story.core.domain.subscription.SubscriptionEventProducer
import com.story.core.domain.subscription.SubscriptionSubscriber
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SubscriptionUpsertHandlerTest : StringSpec({

    val subscriptionSubscriber = mockk<SubscriptionSubscriber>()
    val subscriptionCountManager = mockk<SubscriptionCountManager>(relaxed = true)
    val subscriptionEventProducer = mockk<SubscriptionEventProducer>(relaxed = true)
    val componentCheckHandler = mockk<ComponentCheckHandler>(relaxed = true)

    val handler = SubscriptionUpsertHandler(
        subscriptionSubscriber = subscriptionSubscriber,
        subscriptionCountManager = subscriptionCountManager,
        subscriptionEventProducer = subscriptionEventProducer,
        componentCheckHandler = componentCheckHandler,
    )

    "구독 컴포넌트가 존재하는지 검증한다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"

        val targetId = "462b0543-e66c-45d7-b937-f93743246df8"
        val subscriberId = "f78d5490-b2e5-4cf8-943c-765d1ef8c815"

        coEvery {
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = any(),
            )
        } returns true

        // when
        handler.upsertSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = false,
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

    "자기 자신을 스스로 구독 할 수 없다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"

        val targetId = "462b0543-e66c-45d7-b937-f93743246df8"

        // when & then
        shouldThrowExactly<SubscribeSelfForbiddenException> {
            handler.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = targetId,
                alarm = false,
            )
        }
    }

    "신규 구독인 경우 구독 수를 증가시킨다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"

        val targetId = "462b0543-e66c-45d7-b937-f93743246df8"
        val subscriberId = "f78d5490-b2e5-4cf8-943c-765d1ef8c815"

        coEvery {
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = any(),
            )
        } returns true

        // when
        handler.upsertSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = false,
        )

        // then
        coVerify(exactly = 1) {
            subscriptionCountManager.increase(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        }
    }

    "신규 구독이 아닌 경우 구독 수를 증가시키지 않는다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"

        val targetId = "462b0543-e66c-45d7-b937-f93743246df8"
        val subscriberId = "f78d5490-b2e5-4cf8-943c-765d1ef8c815"

        coEvery {
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = any(),
            )
        } returns false

        // when
        handler.upsertSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = false,
        )

        // then
        coVerify(exactly = 0) {
            subscriptionCountManager.increase(
                workspaceId = any(),
                componentId = any(),
                targetId = any(),
                subscriberId = any(),
            )
        }
        coVerify(exactly = 0) {
            subscriptionCountManager.increase(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        }
    }

    "신규 구독인 경우 구독 생성 이벤트를 발행한다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"

        val targetId = "354b5b46-e533-49f2-a072-545c00f1ebf7"
        val subscriberId = "adeb0d16-6737-4088-b9ec-c43983fe236b"

        coEvery {
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = any(),
            )
        } returns true

        // when
        handler.upsertSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = false,
        )

        // then
        coVerify(exactly = 1) {
            subscriptionEventProducer.publishCreatedEvent(
                workspaceId = any(),
                componentId = any(),
                targetId = any(),
                subscriberId = any(),
                now = any(),
            )
        }
    }

    "신규 구독이 아닌 경우 구독 생성 이벤트를 발행하지 않는다" {
        // given
        val workspaceId = "story"
        val componentId = "follow"

        val targetId = "354b5b46-e533-49f2-a072-545c00f1ebf7"
        val subscriberId = "adeb0d16-6737-4088-b9ec-c43983fe236b"

        coEvery {
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = any(),
            )
        } returns false

        // when
        handler.upsertSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = false,
        )

        // then
        coVerify(exactly = 0) {
            subscriptionEventProducer.publishCreatedEvent(
                workspaceId = any(),
                componentId = any(),
                targetId = any(),
                subscriberId = any(),
                now = any(),
            )
        }
    }

})
