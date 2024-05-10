package com.story.core.domain.file

data class File(
    val fileId: Long,
    val domain: String,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
) {

    companion object {
        fun of(file: FileEntity, domain: String) = File(
            fileId = file.key.fileId,
            domain = domain,
            path = file.path,
            width = file.width,
            height = file.height,
            fileSize = file.fileSize,
        )
    }

}
