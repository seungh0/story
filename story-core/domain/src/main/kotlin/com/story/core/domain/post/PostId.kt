package com.story.core.domain.post

import java.util.Base64
import java.util.StringJoiner

data class PostId(
    val spaceId: String,
    val parentId: String?,
    val depth: Int,
    val postNo: Long,
) {

    fun parentPostId(): PostId? {
        if (parentId.isNullOrBlank()) {
            return null
        }
        return PostId.parsed(parentId)
    }

    fun serialize(): String {
        return ENCODER.encodeToString(
            StringJoiner("-")
                .add(ENCODER.encodeToString(spaceId.toByteArray()))
                .add(if (parentId == null) null else ENCODER.encodeToString(parentId.toByteArray()))
                .add(depth.toString())
                .add(postNo.toString())
                .toString()
                .toByteArray()
        )
    }

    companion object {
        private val ENCODER = Base64.getUrlEncoder()
        private val DECODER = Base64.getUrlDecoder()

        fun parsed(key: String): PostId {
            val elements = String(DECODER.decode(key)).split("-")
            return PostId(
                spaceId = String(DECODER.decode(elements[0])),
                parentId = getParentId(elements),
                depth = elements[2].toInt(),
                postNo = elements[3].toLong(),
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
