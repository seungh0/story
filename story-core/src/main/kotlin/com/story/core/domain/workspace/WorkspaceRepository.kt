package com.story.core.domain.workspace

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface WorkspaceRepository : CassandraBasicRepository<Workspace, String>
