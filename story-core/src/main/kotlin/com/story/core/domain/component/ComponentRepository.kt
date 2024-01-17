package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface ComponentRepository : CassandraBasicRepository<Component, ComponentPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): Flow<Component>

    fun findAllByKeyWorkspaceIdAndKeyResourceId(
        workspaceId: String,
        resourceId: ResourceId,
        pageable: Pageable,
    ): Flow<Component>

}
