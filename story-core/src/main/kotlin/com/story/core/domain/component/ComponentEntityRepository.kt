package com.story.core.domain.component

import com.story.core.domain.component.storage.ComponentCassandraRepository
import com.story.core.domain.component.storage.ComponentEntity
import com.story.core.domain.component.storage.ComponentPrimaryKey
import com.story.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ComponentEntityRepository(
    private val componentCassandraRepository: ComponentCassandraRepository,
) : ComponentReadRepository, ComponentWriteRepository {

    override suspend fun create(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String,
        status: ComponentStatus,
    ): Component {
        val entity = ComponentEntity.of(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = status,
        )
        componentCassandraRepository.save(entity)
        return Component.of(entity)
    }

    override suspend fun update(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): Component {
        val entity = componentCassandraRepository.findById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
            ?: throw ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 리소스($resourceId) 컴포넌트($componentId)입니다.")

        entity.patch(description = description, status = status)

        componentCassandraRepository.save(entity)
        return Component.of(entity)
    }

    override suspend fun existsById(workspaceId: String, resourceId: ResourceId, componentId: String): Boolean {
        return componentCassandraRepository.existsById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
    }

    override suspend fun findById(workspaceId: String, resourceId: ResourceId, componentId: String): Component? {
        val entity = componentCassandraRepository.findById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
        return entity?.let { Component.of(entity) }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyResourceId(
        workspaceId: String,
        resourceId: ResourceId,
        pageable: Pageable,
    ): List<Component> {
        val entities = componentCassandraRepository.findAllByKeyWorkspaceIdAndKeyResourceId(
            workspaceId = workspaceId,
            resourceId = resourceId,
            pageable = pageable,
        ).toList()
        return entities.map { entity -> Component.of(entity) }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): List<Component> {
        val entities = componentCassandraRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            pageable = pageable,
        ).toList()
        return entities.map { entity -> Component.of(entity) }
    }

}
