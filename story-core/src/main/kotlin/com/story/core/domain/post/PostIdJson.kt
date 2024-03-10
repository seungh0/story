package com.story.core.domain.post

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class PostIdJsonDeserializer : StdDeserializer<PostId>(PostId::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): PostId {
        return PostId.parsed(p.valueAsString)
    }
}

class PostIdJsonSerializer : StdSerializer<PostId>(PostId::class.java) {
    override fun serialize(value: PostId, gen: JsonGenerator, provider: SerializerProvider?) {
        gen.writeString(value.serialize())
    }
}
