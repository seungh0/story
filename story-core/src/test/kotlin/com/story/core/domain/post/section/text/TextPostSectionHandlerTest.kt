package com.story.core.domain.post.section.text

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize

class TextPostSectionHandlerTest : StringSpec({

    val handler = TextPostSectionHandler()

    "TextPostSectionContentRequest -> TextPostSectionContent" {
        // given
        val section1 = TextPostSectionContentRequest(
            content = "토키에요",
            priority = 1,
        )
        val section2 = TextPostSectionContentRequest(
            content = "뽀미에요",
            priority = 2,
        )

        // when
        val sut = handler.makeContents(workspaceId = "1", requests = listOf(section1, section2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            section1 to TextPostSectionContent(
                content = "토키에요"
            ),
            section2 to TextPostSectionContent(
                content = "뽀미에요"
            )
        )
    }

    "TextPostSectionContent -> TextPostSectionContentResponse" {
        // given
        val content1 = TextPostSectionContent(
            content = "토키에요"
        )
        val content2 = TextPostSectionContent(
            content = "뽀미에요"
        )

        // when
        val sut = handler.makeContentResponse(listOf(content1, content2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            content1 to TextPostSectionContentResponse(
                content = "토키에요"
            ),
            content2 to TextPostSectionContentResponse(
                content = "뽀미에요"
            )
        )
    }

})
