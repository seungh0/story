package com.story.platform.core.domain.post

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PostTest : FunSpec({

    context("포스트 변경") {
        test("제목만 변경이 있는 경우 변경점이 존재한다") {
            // given
            val post = PostFixture.create()

            // when
            val hasChanged = post.patch(
                title = "title",
                content = null,
            )

            // then
            hasChanged shouldBe true
        }

        test("제목이 동일한 경우 변경점이 존재하지 않는다") {
            // given
            val post = PostFixture.create()

            // when
            val hasChanged = post.patch(
                title = post.title,
                content = null,
            )

            // then
            hasChanged shouldBe false
        }

        test("모든 필드가 변경되는 경우 변경점이 존재한다") {
            // given
            val post = PostFixture.create()

            // when
            val hasChanged = post.patch(
                title = "title",
                content = "content",
            )

            // then
            hasChanged shouldBe true
        }
    }

})
