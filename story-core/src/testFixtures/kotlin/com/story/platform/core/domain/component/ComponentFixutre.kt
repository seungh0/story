package com.story.platform.core.domain.component

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.support.RandomGenerator

object ComponentFixutre {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        resourceId: ResourceId = RandomGenerator.generateEnum(ResourceId::class.java),
        componentId: String = RandomGenerator.generateString(),
        status: ComponentStatus = RandomGenerator.generateEnum(ComponentStatus::class.java),
        description: String = RandomGenerator.generateString(),
    ) = Component(
        key = ComponentPrimaryKey(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        ),
        status = status,
        description = description,
        auditingTime = AuditingTime.created(),
    )

}
