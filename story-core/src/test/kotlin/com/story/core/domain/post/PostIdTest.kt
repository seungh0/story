package com.story.core.domain.post

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PostIdTest : StringSpec({

    "PostId -> keyString" {
        // given
        val spaceId = "user1"
        val parentId: String? = null
        val depth = 1
        val postId = 10000L
        val key = PostId(
            spaceId = spaceId,
            parentId = parentId,
            depth = depth,
            postNo = postId,
        )

        // when
        val keyString = key.serialize()
        val sut = PostId.parsed(keyString)

        // then
        sut shouldBe key
    }

})
