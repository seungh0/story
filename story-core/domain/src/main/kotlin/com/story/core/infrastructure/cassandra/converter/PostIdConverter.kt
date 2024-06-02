package com.story.core.infrastructure.cassandra.converter

import com.story.core.domain.post.PostId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@WritingConverter
class PostIdWriteConverter : Converter<PostId, String> {

    override fun convert(key: PostId): String = key.serialize()

}

@Component
@ReadingConverter
class PostIdReadConverter : Converter<String, PostId> {

    override fun convert(versionStr: String) = PostId.parsed(versionStr)

}
