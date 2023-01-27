package com.story.platform.core.common.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@WritingConverter
class VersionWriteConverter : Converter<com.story.platform.core.common.model.Version, String> {

    override fun convert(version: com.story.platform.core.common.model.Version): String = version.toString()

}

@Component
@ReadingConverter
class VersionReadConverter : Converter<String, com.story.platform.core.common.model.Version> {

    override fun convert(versionStr: String) = com.story.platform.core.common.model.Version.of(versionStr)

}
