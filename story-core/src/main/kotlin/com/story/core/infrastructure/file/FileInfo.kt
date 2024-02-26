package com.story.core.infrastructure.file

data class FileInfo(
    val domain: String,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
)
