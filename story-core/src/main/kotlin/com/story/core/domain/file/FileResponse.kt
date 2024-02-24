package com.story.core.domain.file

data class FileResponse(
    val fileId: Long,
    val domain: String,
    val path: String,
) {

    companion object {
        fun of(file: File, domain: String) = FileResponse(
            fileId = file.key.fileId,
            domain = domain,
            path = file.path + "/" + file.fileName,
        )
    }

}
