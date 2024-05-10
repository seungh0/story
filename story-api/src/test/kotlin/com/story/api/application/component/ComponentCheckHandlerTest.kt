package com.story.api.application.component

import com.story.core.domain.component.Component
import com.story.core.domain.component.ComponentFixutre
import com.story.core.domain.component.ComponentNotExistsException
import com.story.core.domain.component.ComponentRetriever
import com.story.core.domain.component.ComponentStatus
import com.story.core.domain.resource.ResourceId
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.mockk
import java.util.Optional

class ComponentCheckHandlerTest : FunSpec({

    val componentRetriever = mockk<ComponentRetriever>()
    val componentCheckHandler = ComponentCheckHandler(
        componentRetriever = componentRetriever,
    )

    context("컴포넌트를 검증합니다") {
        test("활성화 중인 컴포넌트인 경우 통과한다") {
            // given
            val workspaceId = "story"
            val resourceId = ResourceId.SUBSCRIPTIONS
            val componentId = "following"

            coEvery {
                componentRetriever.getComponent(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                )
            } returns Optional.of(
                Component.of(
                    ComponentFixutre.create(
                        workspaceId = workspaceId,
                        resourceId = resourceId,
                        componentId = componentId,
                        status = ComponentStatus.ENABLED,
                    )
                )
            )

            // when & then
            shouldNotThrowAny {
                componentCheckHandler.checkExistsComponent(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                )
            }
        }

        test("비활성화 중인 컴포넌트인 경우 검증에 실패한다") {
            // given
            val workspaceId = "story"
            val resourceId = ResourceId.SUBSCRIPTIONS
            val componentId = "following"

            coEvery {
                componentRetriever.getComponent(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                )
            } returns Optional.of(
                Component.of(
                    ComponentFixutre.create(
                        workspaceId = workspaceId,
                        resourceId = resourceId,
                        componentId = componentId,
                        status = ComponentStatus.DISABLED,
                    )
                )
            )

            // when & then
            shouldThrowExactly<ComponentNotExistsException> {
                componentCheckHandler.checkExistsComponent(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                )
            }
        }

        test("컴포넌트가 없는 경우 검증에 실패한다") {
            // given
            val workspaceId = "story"
            val resourceId = ResourceId.SUBSCRIPTIONS
            val componentId = "following"

            coEvery {
                componentRetriever.getComponent(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                )
            } throws ComponentNotExistsException("")

            // when & then
            shouldThrowExactly<ComponentNotExistsException> {
                componentCheckHandler.checkExistsComponent(
                    workspaceId = workspaceId,
                    resourceId = resourceId,
                    componentId = componentId,
                )
            }
        }
    }

})
