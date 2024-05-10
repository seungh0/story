package com.story.core.domain.post.section.link

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize

class LinkPostSectionHandlerTest : StringSpec({

    val handler = LinkPostSectionHandler()

    "LinkPostSectionContentRequest -> LinkPostSectionContent" {
        // given
        val section1 = LinkPostSectionContentRequest(
            priority = 1,
            link = "https://google.com",
            extra = mapOf(
                "og:image" to "https://google.com/icon.png",
                "og:title" to "google",
                "og:description" to "google home",
            ),
        )
        val section2 = LinkPostSectionContentRequest(
            priority = 2,
            link = "https://naver.com",
            extra = mapOf(
                "og:image" to "https://naver.com/icon.png",
                "og:title" to "naver",
                "og:description" to "naver home",
            ),
        )

        // when
        val sut = handler.makeContents(workspaceId = "1", requests = listOf(section1, section2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            section1 to LinkPostSectionContentEntity(
                link = "https://google.com",
                extra = mapOf(
                    "og:image" to "https://google.com/icon.png",
                    "og:title" to "google",
                    "og:description" to "google home",
                ),
            ),
            section2 to LinkPostSectionContentEntity(
                link = "https://naver.com",
                extra = mapOf(
                    "og:image" to "https://naver.com/icon.png",
                    "og:title" to "naver",
                    "og:description" to "naver home",
                ),
            )
        )
    }

    "LinkPostSectionContent -> LinkPostSectionContentResponse" {
        // given
        val content1 = LinkPostSectionContentEntity(
            link = "https://google.com",
            extra = mapOf(
                "og:image" to "https://google.com/icon.png",
                "og:title" to "google",
                "og:description" to "google home",
            ),
        )
        val content2 = LinkPostSectionContentEntity(
            link = "https://naver.com",
            extra = mapOf(
                "og:image" to "https://naver.com/icon.png",
                "og:title" to "naver",
                "og:description" to "naver home",
            ),
        )

        // when
        val sut = handler.makeContentResponse(listOf(content1, content2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            content1 to LinkPostSectionContent(
                link = "https://google.com",
                extra = mapOf(
                    "og:image" to "https://google.com/icon.png",
                    "og:title" to "google",
                    "og:description" to "google home",
                ),
            ),
            content2 to LinkPostSectionContent(
                link = "https://naver.com",
                extra = mapOf(
                    "og:image" to "https://naver.com/icon.png",
                    "og:title" to "naver",
                    "og:description" to "naver home",
                ),
            )
        )
    }

})
