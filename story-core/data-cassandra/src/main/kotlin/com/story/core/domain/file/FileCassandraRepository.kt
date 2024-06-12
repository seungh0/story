package com.story.core.domain.file

import com.story.core.support.cassandra.CassandraBasicRepository

interface FileCassandraRepository : CassandraBasicRepository<FileEntity, FilePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFileTypeAndKeySlotIdAndKeyFileIdIn(
        workspaceId: String,
        fileType: FileType,
        slotId: Long,
        fileIds: Collection<Long>,
    ): List<FileEntity>

}
