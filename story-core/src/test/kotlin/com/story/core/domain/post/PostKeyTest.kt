package com.story.core.domain.post

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PostKeyTest : StringSpec({

    "PostKey -> keyString" {
        // given
        val spaceId = "user1"
        val parentId: String? = null
        val depth = 1
        val postId = 10000L
        val key = PostKey(
            spaceId = spaceId,
            parentId = parentId,
            depth = depth,
            postId = postId,
        )

        // when
        val keyString = key.serialize()
        val sut = PostKey.parsed(keyString)

        // then
        sut shouldBe key
    }

})
