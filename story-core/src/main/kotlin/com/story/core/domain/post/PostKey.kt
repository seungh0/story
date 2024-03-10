package com.story.core.domain.post

import java.util.Base64
import java.util.StringJoiner

data class PostKey(
    val spaceId: String,
    val parentKey: String?,
    val depth: Int,
    val postId: Long,
) {

    fun serialize(): String {
        return ENCODER.encodeToString(
            StringJoiner("-")
                .add(ENCODER.encodeToString(spaceId.toByteArray()))
                .add(if (parentKey == null) null else ENCODER.encodeToString(parentKey.toByteArray()))
                .add(depth.toString())
                .add(postId.toString())
                .toString()
                .toByteArray()
        )
    }

    companion object {
        private val ENCODER = Base64.getUrlEncoder()
        private val DECODER = Base64.getUrlDecoder()

        fun parsed(key: String): PostKey {
            val elements = String(DECODER.decode(key)).split("-")
            return PostKey(
                spaceId = String(DECODER.decode(elements[0])),
                parentKey = getParentId(elements),
                depth = elements[2].toInt(),
                postId = elements[3].toLong(),
            )
        }

        private fun getParentId(elements: List<String>): String? {
            val parentId = elements[1]
            if (parentId == "null") {
                return null
            }
            return String(DECODER.decode(parentId))
        }
    }

}
