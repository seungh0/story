package com.story.platform.core.domain.workspace

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface WorkspaceRepository : CoroutineCrudRepository<Workspace, String>
