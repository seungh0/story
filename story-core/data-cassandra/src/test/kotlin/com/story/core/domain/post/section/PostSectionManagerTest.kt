package com.story.core.domain.post.section

import com.story.core.IntegrationTest
import com.story.core.common.json.toJson
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.post.section.link.LinkPostSectionContentCommand
import com.story.core.domain.post.section.link.LinkPostSectionContentEntity
import com.story.core.domain.post.section.text.TextPostSectionContentCommand
import com.story.core.domain.post.section.text.TextPostSectionContentEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@IntegrationTest
class PostSectionManagerTest(
    private val postSectionManager: PostSectionManager,
) : StringSpec({

    "포스트 섹션을 생성합니다" {
        // given
        val request1 = TextPostSectionContentCommand(
            content = "토키에요",
            priority = 1,
        )
        val request2 = TextPostSectionContentCommand(
            content = "뽀미에요",
            priority = 2,
        )

        val request3 = LinkPostSectionContentCommand(
            priority = 3,
            link = "https://google.com",
            extra = emptyMap(),
        )

        // when
        val sut = postSectionManager.makePostSections(
            requests = listOf(request1, request2, request3),
            postSpaceKey = PostSpaceKey(
                workspaceId = "story",
                componentId = "user-post",
                spaceId = "tokki",
            ),
            ownerId = "tokki",
            postNo = 1000L,
            parentId = null,
        )

        // then
        sut shouldHaveSize 3
        sut.forEach { section ->
            section.key.workspaceId shouldBe "story"
            section.key.componentId shouldBe "user-post"
            section.key.spaceId shouldBe "tokki"
            section.key.parentId shouldBe ""
            section.key.slotId shouldBe 10L
            section.key.postNo shouldBe 1000L
        }

        sut[0].sectionType shouldBe PostSectionType.TEXT
        sut[0].key.priority shouldBe 1L
        sut[0].data shouldBe TextPostSectionContentEntity(content = "토키에요", extra = emptyMap()).toJson()

        sut[1].sectionType shouldBe PostSectionType.TEXT
        sut[1].key.priority shouldBe 2L
        sut[1].data shouldBe TextPostSectionContentEntity(content = "뽀미에요", extra = emptyMap()).toJson()

        sut[2].sectionType shouldBe PostSectionType.LINK
        sut[2].key.priority shouldBe 3L
        sut[2].data shouldBe LinkPostSectionContentEntity(
            link = "https://google.com",
            extra = emptyMap(),
        ).toJson()
    }

})
