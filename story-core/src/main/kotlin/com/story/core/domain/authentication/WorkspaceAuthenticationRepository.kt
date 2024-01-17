package com.story.core.domain.authentication

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface WorkspaceAuthenticationRepository :
    CassandraBasicRepository<WorkspaceAuthentication, WorkspaceAuthenticationPrimaryKey>
