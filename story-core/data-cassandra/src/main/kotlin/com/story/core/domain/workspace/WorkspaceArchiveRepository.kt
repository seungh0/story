package com.story.core.domain.workspace

import com.story.core.support.cassandra.CassandraBasicRepository

interface WorkspaceArchiveRepository : CassandraBasicRepository<WorkspaceArchiveEntity, String>
