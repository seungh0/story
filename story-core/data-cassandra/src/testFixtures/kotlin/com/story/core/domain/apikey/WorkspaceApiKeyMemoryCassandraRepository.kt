package com.story.core.domain.apikey

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import com.story.core.lib.StubCassandraBasicRepository

class WorkspaceApiKeyMemoryCassandraRepository :
    WorkspaceApiKeyCassandraRepository,
    CassandraBasicRepository<WorkspaceApiKeyEntity, WorkspaceApiKeyPrimaryKey> by StubCassandraBasicRepository()
