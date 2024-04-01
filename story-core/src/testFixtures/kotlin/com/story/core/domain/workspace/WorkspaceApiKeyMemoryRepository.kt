package com.story.core.domain.workspace

import com.story.core.domain.apikey.WorkspaceApiKey
import com.story.core.domain.apikey.WorkspaceApiKeyPrimaryKey
import com.story.core.domain.apikey.WorkspaceApiKeyRepository
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import com.story.core.lib.StubCassandraBasicRepository

class WorkspaceApiKeyMemoryRepository :
    WorkspaceApiKeyRepository,
    CassandraBasicRepository<WorkspaceApiKey, WorkspaceApiKeyPrimaryKey> by StubCassandraBasicRepository()
