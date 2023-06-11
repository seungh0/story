package com.story.platform.core.domain.post

data class PostKey(
    val spaceType: PostSpaceType,
    val spaceId: String,
    val postId: Long,
) {

    companion object {
        private const val SPLITTER = ":"

        fun of(key: String): PostKey {
            // TODO: 개선
            val (spaceType, spaceId, postId) = key.split(SPLITTER)

            return PostKey(
                spaceType = PostSpaceType.valueOf(spaceType),
                spaceId = spaceId,
                postId = postId.toLongOrNull() ?: throw IllegalArgumentException("")
            )
        }
    }

}
