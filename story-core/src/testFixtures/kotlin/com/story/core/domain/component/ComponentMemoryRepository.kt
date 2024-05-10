package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import com.story.core.lib.StubCassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

class ComponentMemoryRepository :
    ComponentRepository,
    CassandraBasicRepository<ComponentEntity, ComponentPrimaryKey> by StubCassandraBasicRepository() {

    override fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): Flow<ComponentEntity> {
        TODO("Not yet implemented")
    }

    override fun findAllByKeyWorkspaceIdAndKeyResourceId(
        workspaceId: String,
        resourceId: ResourceId,
        pageable: Pageable,
    ): Flow<ComponentEntity> {
        TODO("Not yet implemented")
    }

}
