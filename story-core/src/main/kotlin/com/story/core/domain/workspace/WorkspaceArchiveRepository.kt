package com.story.core.domain.workspace

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface WorkspaceArchiveRepository : CoroutineCrudRepository<WorkspaceArchive, String>
