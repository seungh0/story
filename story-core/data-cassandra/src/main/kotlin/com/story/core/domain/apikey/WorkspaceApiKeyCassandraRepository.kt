package com.story.core.domain.apikey

import com.story.core.support.cassandra.CassandraBasicRepository

interface WorkspaceApiKeyCassandraRepository : CassandraBasicRepository<WorkspaceApiKeyEntity, WorkspaceApiKeyPrimaryKey>
