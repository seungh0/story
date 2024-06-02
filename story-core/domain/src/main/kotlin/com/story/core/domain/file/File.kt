package com.story.core.domain.file

data class File(
    val fileId: Long,
    val domain: String,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
)
