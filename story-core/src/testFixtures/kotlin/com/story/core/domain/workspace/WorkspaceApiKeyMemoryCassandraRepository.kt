package com.story.core.domain.workspace

import com.story.core.domain.apikey.storage.WorkspaceApiKey
import com.story.core.domain.apikey.storage.WorkspaceApiKeyCassandraRepository
import com.story.core.domain.apikey.storage.WorkspaceApiKeyPrimaryKey
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import com.story.core.lib.StubCassandraBasicRepository

class WorkspaceApiKeyMemoryCassandraRepository :
    WorkspaceApiKeyCassandraRepository,
    CassandraBasicRepository<WorkspaceApiKey, WorkspaceApiKeyPrimaryKey> by StubCassandraBasicRepository()
