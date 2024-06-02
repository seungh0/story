package com.story.core.domain.workspace

import com.story.core.common.model.dto.AuditingTimeResponse
import java.time.Duration
import java.time.LocalDateTime

data class WorkspaceArchive(
    val workspaceId: String,
    val name: String,
    val plan: WorkspacePricePlan,
    val archiveTime: LocalDateTime,
) : AuditingTimeResponse() {

    fun canPurge(now: LocalDateTime): Boolean = this.archiveTime <= now.minus(MIN_RETENTION_DURATION)

    companion object {
        private val MIN_RETENTION_DURATION = Duration.ofDays(90)
    }

}
