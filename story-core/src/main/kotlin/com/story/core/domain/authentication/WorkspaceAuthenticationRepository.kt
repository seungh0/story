package com.story.core.domain.authentication

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface WorkspaceAuthenticationRepository :
    CoroutineCrudRepository<WorkspaceAuthentication, WorkspaceAuthenticationPrimaryKey>
