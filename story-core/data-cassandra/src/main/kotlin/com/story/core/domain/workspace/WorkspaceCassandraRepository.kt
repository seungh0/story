package com.story.core.domain.workspace

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface WorkspaceCassandraRepository : CassandraBasicRepository<WorkspaceEntity, String>
