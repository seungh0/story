package com.story.platform.core.domain.post

data class PostKey(
    val spaceId: String,
    val postId: Long,
) {

    companion object {
        private const val SPLITTER = ":"

        fun of(key: String): PostKey {
            // TODO: 개선
            val (spaceId, postId) = key.split(SPLITTER)

            return PostKey(
                spaceId = spaceId,
                postId = postId.toLongOrNull() ?: throw IllegalArgumentException("잘못된 PostId 입니다")
            )
        }
    }

}
