package com.story.core.domain.workspace

import com.story.core.support.cassandra.CassandraBasicRepository

interface WorkspaceCassandraRepository : CassandraBasicRepository<WorkspaceEntity, String>
