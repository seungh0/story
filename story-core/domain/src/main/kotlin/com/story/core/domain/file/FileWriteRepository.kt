package com.story.core.domain.file

import com.story.core.support.file.FileInfo

interface FileWriteRepository {

    suspend fun saveAll(workspaceId: String, fileType: FileType, fileInfos: Collection<FileInfo>): List<File>

}
