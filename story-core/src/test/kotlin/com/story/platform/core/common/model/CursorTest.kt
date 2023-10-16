package com.story.platform.core.common.model

import com.story.platform.core.common.model.dto.CursorResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CursorTest : StringSpec({

    "커서가 null인 경우 다음 커서가 없다" {
        // given
        val cursor = CursorResponse.of(null)

        // when
        val sut = cursor.hasNext

        // then
        sut shouldBe false
    }

    "커서가 null이 아닌 경우 다음 커서가 있다" {
        // given
        val cursor = CursorResponse.of("cursor")

        // when
        val sut = cursor.hasNext

        // then
        sut shouldBe true
    }

})
