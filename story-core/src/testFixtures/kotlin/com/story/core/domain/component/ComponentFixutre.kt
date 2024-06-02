package com.story.core.domain.component

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.domain.component.storage.ComponentEntity
import com.story.core.domain.component.storage.ComponentPrimaryKey
import com.story.core.domain.resource.ResourceId
import com.story.core.support.RandomGenerator

object ComponentFixutre {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        resourceId: ResourceId = RandomGenerator.generateEnum(ResourceId::class.java),
        componentId: String = RandomGenerator.generateString(),
        status: ComponentStatus = RandomGenerator.generateEnum(ComponentStatus::class.java),
        description: String = RandomGenerator.generateString(),
    ) = ComponentEntity(
        key = ComponentPrimaryKey(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        ),
        status = status,
        description = description,
        auditingTime = AuditingTimeEntity.created(),
    )

}
