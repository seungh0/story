package com.story.core.domain.apikey

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface WorkspaceApiKeyRepository : CassandraBasicRepository<WorkspaceApiKey, WorkspaceApiKeyPrimaryKey>
