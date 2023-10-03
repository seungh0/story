package com.story.platform.core.common.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CursorResultTest : StringSpec({

    "커서가 null인 경우 다음 커서가 없다" {
        // given
        val cursor = ContentsWithCursor.of(data = listOf<String>(), cursor = Cursor.of(null))

        // when
        val sut = cursor.hasNext

        // then
        sut shouldBe false
    }

    "커서가 null이 아닌 경우 다음 커서가 있다" {
        // given
        val cursor = ContentsWithCursor.of(data = listOf<String>(), cursor = Cursor.of("cursor"))

        // when
        val sut = cursor.hasNext

        // then
        sut shouldBe true
    }

})
