package com.story.core.domain.post.section.text

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize

class TextPostSectionHandlerTest : StringSpec({

    val handler = TextPostSectionHandler()

    "TextPostSectionContentCommand -> TextPostSectionContent" {
        // given
        val section1 = TextPostSectionContentCommand(
            content = "토키에요",
            priority = 1,
        )
        val section2 = TextPostSectionContentCommand(
            content = "뽀미에요",
            priority = 2,
        )

        // when
        val sut = handler.makeContents(workspaceId = "1", requests = listOf(section1, section2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            section1 to TextPostSectionContentEntity(
                content = "토키에요",
                extra = emptyMap(),
            ),
            section2 to TextPostSectionContentEntity(
                content = "뽀미에요",
                extra = emptyMap(),
            )
        )
    }

    "TextPostSectionContent -> TextPostSectionContentResponse" {
        // given
        val content1 = TextPostSectionContentEntity(
            content = "토키에요",
            extra = emptyMap(),
        )
        val content2 = TextPostSectionContentEntity(
            content = "뽀미에요",
            extra = emptyMap(),
        )

        // when
        val sut = handler.makeContentResponse(listOf(content1, content2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            content1 to TextPostSectionContent(
                content = "토키에요",
                extra = emptyMap(),
            ),
            content2 to TextPostSectionContent(
                content = "뽀미에요",
                extra = emptyMap(),
            )
        )
    }

})
