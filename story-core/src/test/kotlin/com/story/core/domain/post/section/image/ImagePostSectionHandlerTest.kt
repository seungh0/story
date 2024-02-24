// package com.story.core.domain.post.section.image
//
// import com.story.core.domain.file.FileType
// import com.story.core.infrastructure.file.FileProperties
// import io.kotest.core.spec.style.StringSpec
// import io.kotest.matchers.maps.shouldContainExactly
// import io.kotest.matchers.maps.shouldHaveSize
//
// class ImagePostSectionHandlerTest : StringSpec({
//
//    val handler = ImagePostSectionHandler(
//        properties = FileProperties(
//            properties = mapOf(
//                FileType.IMAGE to FileProperties(
//                    domain = "https://cdn.story.com"
//                )
//            ),
//        )
//    )
//
//    "ImagePostSectionContentRequest -> ImagePostSectionContent" {
//        // given
//        val section1 = ImagePostSectionContentRequest(
//            priority = 1,
//            path = "/pictures",
//            fileName = "flower.png",
//        )
//        val section2 = ImagePostSectionContentRequest(
//            priority = 2,
//            path = "/pictures",
//            fileName = "dog.png",
//        )
//
//        // when
//        val sut = handler.makeContents(listOf(section1, section2))
//
//        // then
//        sut shouldHaveSize 2
//        sut shouldContainExactly mapOf(
//            section1 to ImagePostSectionContent(
//                path = "/pictures",
//                width = 0,
//                height = 0,
//                fileSize = 0,
//                fileName = "flower.png",
//            ),
//            section2 to ImagePostSectionContent(
//                path = "/pictures",
//                width = 0,
//                height = 0,
//                fileSize = 0,
//                fileName = "dog.png",
//            )
//        )
//    }
//
//    "ImagePostSectionContent -> ImagePostSectionContentResponse" {
//        // given
//        val content1 = ImagePostSectionContent(
//            path = "/pictures",
//            width = 120,
//            height = 86,
//            fileSize = 1024,
//            fileName = "flower.png",
//        )
//        val content2 = ImagePostSectionContent(
//            path = "/pictures",
//            width = 120,
//            height = 86,
//            fileSize = 1024,
//            fileName = "dog.png",
//        )
//
//        // when
//        val sut = handler.makeContentResponse(listOf(content1, content2))
//
//        // then
//        sut shouldHaveSize 2
//        sut shouldContainExactly mapOf(
//            content1 to ImagePostSectionContentResponse(
//                path = "/pictures/flower.png",
//                width = 120,
//                height = 86,
//                fileSize = 1024,
//                domain = "https://cdn.story.com",
//            ),
//            content2 to ImagePostSectionContentResponse(
//                path = "/pictures/dog.png",
//                width = 120,
//                height = 86,
//                fileSize = 1024,
//                domain = "https://cdn.story.com",
//            )
//        )
//    }
//
// })
