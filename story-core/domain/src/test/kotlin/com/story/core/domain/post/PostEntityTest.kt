package com.story.core.domain.post

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PostEntityTest : FunSpec({

    context("포스트 변경") {
        test("제목만 변경이 있는 경우 변경점이 존재한다") {
            // given
            val post = PostFixture.create(
                extra = emptyMap(),
            )

            // when
            val hasChanged = post.patch(
                title = "title",
                extra = emptyMap(),
            )

            // then
            hasChanged shouldBe true
        }

        test("extra만 변경이 있는 경우 변경점이 존재한다") {
            // given
            val post = PostFixture.create(
                extra = emptyMap(),
            )

            // when
            val hasChanged = post.patch(
                title = post.title,
                extra = mapOf(
                    "commentEnabled" to "true"
                ),
            )

            // then
            hasChanged shouldBe true
        }

        test("제목이 동일한 경우 변경점이 존재하지 않는다") {
            // given
            val post = PostFixture.create(
                extra = emptyMap(),
            )

            // when
            val hasChanged = post.patch(
                title = post.title,
                extra = emptyMap(),
            )

            // then
            hasChanged shouldBe false
        }

        test("모든 필드가 null로 patch 요청하는 경우 변경점이 존재하지 않는다") {
            // given
            val post = PostFixture.create(
                extra = emptyMap(),
            )

            // when
            val hasChanged = post.patch(
                title = null,
                extra = null,
            )

            // then
            hasChanged shouldBe false
        }
    }

})
