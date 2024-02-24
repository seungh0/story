package com.story.core.domain.file

data class FileResponse(
    val fileId: Long,
    val domain: String,
    val path: String,
    val fileName: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
) {

    companion object {
        fun of(file: File, domain: String) = FileResponse(
            fileId = file.key.fileId,
            domain = domain,
            path = file.path,
            fileName = file.fileName,
            width = file.width,
            height = file.height,
            fileSize = file.fileSize,
        )
    }

}
