package com.story.platform.core.domain.event

import com.story.platform.core.common.error.ForbiddenException
import com.story.platform.core.domain.resource.ResourceId
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class EventActionTest : FunSpec({

    context("특정 리소스에서 사용할 수 있는 이벤트 행위인지 검증한다") {
        test("사용할 수 있는 이벤트 액션인 경우 pass") {
            // given
            val resourceId = ResourceId.SUBSCRIPTIONS
            val eventAction = EventAction.CREATED

            // when & then
            shouldNotThrowAny { eventAction.validateActionForResource(resourceId) }
        }

        test("사용할 수 없는 이벤트 액션인 경우 Forbidden 예외가 발생한다") {
            // given
            val resourceId = ResourceId.SUBSCRIPTIONS
            val eventAction = EventAction.UPDATED

            // when & then
            shouldThrowExactly<ForbiddenException> { eventAction.validateActionForResource(resourceId) }
        }
    }

    context("특정 리소스에서 사용할 수 있는 이벤트 행위 목록을 조회한다") {
        test("[구독]에서 사용할 수 있는 이벤트 목록을 조회한다") {
            // given
            val resourceId = ResourceId.SUBSCRIPTIONS

            // when
            val sut = EventAction.getAvailableActions(resourceId = resourceId)

            // then
            sut shouldContainExactlyInAnyOrder setOf(EventAction.CREATED, EventAction.DELETED)
        }

        test("[포스트]에서 사용할 수 있는 이벤트 목록을 조회한다") {
            // given
            val resourceId = ResourceId.POSTS

            // when
            val sut = EventAction.getAvailableActions(resourceId = resourceId)

            // then
            sut shouldContainExactlyInAnyOrder setOf(EventAction.CREATED, EventAction.UPDATED, EventAction.DELETED)
        }
    }

})
