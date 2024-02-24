package com.story.core.domain.file

import com.story.core.infrastructure.file.FileInfo
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("file_v1")
data class File(
    @field:PrimaryKey
    val key: FilePrimaryKey,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            fileType: FileType,
            fileInfo: FileInfo,
        ) = File(
            key = FilePrimaryKey.of(
                workspaceId = workspaceId,
                fileType = fileType,
                fileId = FileIdHelper.generate(),
            ),
            path = fileInfo.path,
            width = fileInfo.width,
            height = fileInfo.height,
            fileSize = fileInfo.fileSize,
            fileName = fileInfo.fileName,
        )
    }

}

@PrimaryKeyClass
data class FilePrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val fileType: FileType,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val fileId: Long,
) {

    companion object {
        fun of(
            workspaceId: String,
            fileType: FileType,
            fileId: Long,
        ) = FilePrimaryKey(
            workspaceId = workspaceId,
            fileType = fileType,
            slotId = FileIdHelper.getSlot(id = fileId),
            fileId = fileId,
        )
    }

}
