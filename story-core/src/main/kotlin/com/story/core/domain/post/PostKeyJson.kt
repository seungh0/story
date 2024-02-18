package com.story.core.domain.post

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class PostKeyJsonDeserializer : StdDeserializer<PostKey>(PostKey::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): PostKey {
        return PostKey.parsed(p.valueAsString)
    }
}

class PostKeyJsonSerializer : StdSerializer<PostKey>(PostKey::class.java) {
    override fun serialize(value: PostKey, gen: JsonGenerator, provider: SerializerProvider?) {
        gen.writeString(value.serialize())
    }
}
