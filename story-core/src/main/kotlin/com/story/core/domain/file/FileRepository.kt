package com.story.core.domain.file

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface FileRepository : CassandraBasicRepository<FileEntity, FilePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFileTypeAndKeySlotIdAndKeyFileIdIn(
        workspaceId: String,
        fileType: FileType,
        slotId: Long,
        fileIds: Collection<Long>,
    ): List<FileEntity>

}
