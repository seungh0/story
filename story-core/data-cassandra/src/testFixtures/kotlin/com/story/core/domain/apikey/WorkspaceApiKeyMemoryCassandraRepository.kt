package com.story.core.domain.apikey

import com.story.core.lib.StubCassandraBasicRepository
import com.story.core.support.cassandra.CassandraBasicRepository

class WorkspaceApiKeyMemoryCassandraRepository :
    WorkspaceApiKeyCassandraRepository,
    CassandraBasicRepository<WorkspaceApiKeyEntity, WorkspaceApiKeyPrimaryKey> by StubCassandraBasicRepository()
