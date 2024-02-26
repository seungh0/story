package com.story.core.domain.post.section.image

import com.story.core.domain.file.FileResponse
import com.story.core.domain.file.FileRetriever
import com.story.core.domain.file.FileType
import com.story.core.infrastructure.file.FileProperties
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.mockk.coEvery
import io.mockk.mockk

class ImagePostSectionHandlerTest : StringSpec({

    val fileRetriever = mockk<FileRetriever>()

    val handler = ImagePostSectionHandler(
        properties = FileProperties(
            properties = mapOf(
                FileType.IMAGE to FileProperties.Properties(
                    domain = "https://cdn.story.com"
                )
            )
        ),
        fileRetriever = fileRetriever,
    )

    "ImagePostSectionContentRequest -> ImagePostSectionContent" {
        // given
        val section1 = ImagePostSectionContentRequest(
            priority = 1,
            fileId = 1L,
            extra = mapOf(
                "extra" to 10
            ),
        )
        val section2 = ImagePostSectionContentRequest(
            priority = 2,
            fileId = 2L,
            extra = emptyMap(),
        )

        coEvery { fileRetriever.getFiles(any(), any(), any()) } returns mapOf(
            1L to FileResponse(
                path = "/pictures/flower.png",
                width = 40,
                height = 20,
                fileSize = 100,
                domain = "https://cdn.story.com",
                fileId = 1L,
            ),
            2L to FileResponse(
                path = "/pictures/dog.png",
                width = 80,
                height = 60,
                fileSize = 1000,
                domain = "https://cdn.story.com",
                fileId = 2L,
            )
        )

        // when
        val sut = handler.makeContents(workspaceId = "story", requests = listOf(section1, section2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            section1 to ImagePostSectionContent(
                path = "/pictures/flower.png",
                width = 40,
                height = 20,
                fileSize = 100,
                extra = mapOf(
                    "extra" to 10
                ),
            ),
            section2 to ImagePostSectionContent(
                path = "/pictures/dog.png",
                width = 80,
                height = 60,
                fileSize = 1000,
                extra = emptyMap(),
            )
        )
    }

    "ImagePostSectionContent -> ImagePostSectionContentResponse" {
        // given
        val content1 = ImagePostSectionContent(
            path = "/pictures/flower.png",
            width = 120,
            height = 86,
            fileSize = 1024,
            extra = emptyMap(),
        )
        val content2 = ImagePostSectionContent(
            path = "/pictures/dog.png",
            width = 120,
            height = 86,
            fileSize = 1024,
            extra = mapOf(
                "extra" to 10
            ),
        )

        // when
        val sut = handler.makeContentResponse(listOf(content1, content2))

        // then
        sut shouldHaveSize 2
        sut shouldContainExactly mapOf(
            content1 to ImagePostSectionContentResponse(
                path = "/pictures/flower.png",
                width = 120,
                height = 86,
                domain = "https://cdn.story.com",
                extra = emptyMap(),
            ),
            content2 to ImagePostSectionContentResponse(
                path = "/pictures/dog.png",
                width = 120,
                height = 86,
                domain = "https://cdn.story.com",
                extra = mapOf(
                    "extra" to 10
                ),
            )
        )
    }

})
