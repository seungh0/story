package com.story.core.domain.post

import com.story.core.FunSpecIntegrationTest
import com.story.core.IntegrationTest
import com.story.core.common.json.toJson
import com.story.core.domain.post.section.PostSectionRepository
import com.story.core.domain.post.section.PostSectionType
import com.story.core.domain.post.section.text.TextPostSectionContent
import com.story.core.domain.post.section.text.TextPostSectionContentRequest
import com.story.core.domain.reaction.ReactionDistributionKey
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostRegisterTest(
    private val postCreator: PostCreator,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val postSectionRepository: PostSectionRepository,
) : FunSpecIntegrationTest({

    context("신규 포스트를 등록한다") {
        test("유저가 새로운 포스트를 등록합니다") {
            // given
            val postSpaceKey = PostSpaceKey(
                workspaceId = "story",
                componentId = "post",
                spaceId = "commentId",
            )
            val ownerId = "ownerId"
            val title = "포스트 제목"
            val section1 = TextPostSectionContentRequest(
                content = "컨텐츠 내용 - 1",
                priority = 1L,
            )
            val section2 = TextPostSectionContentRequest(
                content = "컨텐츠 내용 - 2",
                priority = 2L,
            )

            // when
            postCreator.createPost(
                postSpaceKey = postSpaceKey,
                ownerId = ownerId,
                title = title,
                sections = listOf(section1, section2),
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.slotId shouldBe 1L
                it.key.postId shouldNotBe null
                it.ownerId shouldBe ownerId
                it.title shouldBe title
            }

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 1
            postReverses[0].also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.distributionKey shouldBe ReactionDistributionKey.makeKey(ownerId)
                it.key.ownerId shouldBe ownerId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.postId shouldNotBe null
                it.title shouldBe title
            }

            val postSections = postSectionRepository.findAll().toList()
            postSections shouldHaveSize 2
            postSections[0].also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.slotId shouldBe 1L
                it.key.priority shouldBe 1L
                it.sectionType shouldBe PostSectionType.TEXT
                it.data shouldBe TextPostSectionContent(
                    content = section1.content
                ).toJson()
            }
            postSections[1].also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.slotId shouldBe 1L
                it.key.priority shouldBe 2L
                it.sectionType shouldBe PostSectionType.TEXT
                it.data shouldBe TextPostSectionContent(
                    content = section2.content
                ).toJson()
            }
        }
    }

})
