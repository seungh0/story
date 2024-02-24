package com.story.core.infrastructure.file

import com.story.core.common.error.NotSupportedException
import com.story.core.domain.file.FileType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("story.photo")
data class FileProperties(
    val properties: Map<FileType, Properties>,
) {

    fun getProperties(fileType: FileType): Properties {
        return properties[fileType] ?: throw NotSupportedException("fileType($fileType)에 등록된 프로퍼티가 존재하지 않습니다")
    }

    data class Properties(
        val domain: String,
    )

}
