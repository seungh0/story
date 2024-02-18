package com.story.core.infrastructure.cassandra.converter

import com.story.core.domain.post.PostKey
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@WritingConverter
class PostKeyWriteConverter : Converter<PostKey, String> {

    override fun convert(key: PostKey): String = key.serialize()

}

@Component
@ReadingConverter
class PostKeyReadConverter : Converter<String, PostKey> {

    override fun convert(versionStr: String) = PostKey.parsed(versionStr)

}
