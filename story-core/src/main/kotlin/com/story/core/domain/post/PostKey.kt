package com.story.core.domain.post

import java.util.Base64
import java.util.StringJoiner

data class PostKey(
    val spaceId: String,
    val parentId: String?,
    val depth: Int,
    val postId: Long,
) {

    fun serialize(): String {
        return encoder.encodeToString(
            StringJoiner("-")
                .add(encoder.encodeToString(spaceId.toByteArray()))
                .add(if (parentId == null) null else encoder.encodeToString(parentId.toByteArray()))
                .add(depth.toString())
                .add(postId.toString())
                .toString()
                .toByteArray()
        )
    }

    companion object {
        private val encoder = Base64.getUrlEncoder()
        private val decoder = Base64.getUrlDecoder()

        fun parsed(key: String): PostKey {
            val elements = String(decoder.decode(key)).split("-")
            return PostKey(
                spaceId = String(decoder.decode(elements[0])),
                parentId = getParentId(elements),
                depth = elements[2].toInt(),
                postId = elements[3].toLong(),
            )
        }

        private fun getParentId(elements: List<String>): String? {
            val parentId = elements[1]
            if (parentId == "null") {
                return null
            }
            return String(decoder.decode(parentId))
        }
    }

}
