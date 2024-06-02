package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

class ComponentCreatorTest : StringSpec({

    val componentRepository = ComponentMemoryCassandraRepository()

    val componentCreator = ComponentCreator(
        componentReadRepository = ComponentEntityRepository(componentRepository),
        componentWriteRepository = ComponentEntityRepository(componentRepository),
    )

    "새로운 컴포넌트를 등록합니다" {
        // given
        val workspaceId = "story"
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "follow"
        val description = "팔로워 시스템"

        // when
        componentCreator.createComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
        )

        // then
        val components = componentRepository.findAll().toList()
        components shouldHaveSize 1
        components.first().also {
            it.key.workspaceId shouldBe workspaceId
            it.key.resourceId shouldBe resourceId
            it.key.componentId shouldBe componentId
            it.description shouldBe description
        }
    }

    "이미 존재하는 컴포넌트인 경우 등록에 실패한다" {
        // given
        val workspaceId = "story"
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "follow"
        val description = "팔로워 시스템"

        componentRepository.save(
            ComponentFixutre.create(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )

        // when & then
        shouldThrowExactly<ComponentAlreadyExistsException> {
            componentCreator.createComponent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                description = description,
            )
        }
    }

})
