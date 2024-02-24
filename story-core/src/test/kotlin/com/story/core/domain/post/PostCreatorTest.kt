package com.story.core.domain.post

import com.story.core.FunSpecIntegrationTest
import com.story.core.IntegrationTest
import com.story.core.common.json.toJson
import com.story.core.domain.post.section.PostSectionRepository
import com.story.core.domain.post.section.PostSectionType
import com.story.core.domain.post.section.text.TextPostSectionContent
import com.story.core.domain.post.section.text.TextPostSectionContentRequest
import com.story.core.domain.reaction.ReactionDistributionKey
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostCreatorTest(
    private val postCreator: PostCreator,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val postSectionRepository: PostSectionRepository,
) : FunSpecIntegrationTest({

    context("신규 포스트를 등록한다") {
        test("새로운 루트의 포스트를 등록합니다") {
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
                parentId = null,
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
                it.key.parentId shouldBe ""
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
                it.key.parentId shouldBe ""
                it.key.postId shouldNotBe null
                it.title shouldBe title
            }

            val postSections = postSectionRepository.findAll().toList()
            postSections shouldHaveSize 2
            postSections[0].also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.parentId shouldBe ""
                it.key.slotId shouldBe 1L
                it.key.priority shouldBe 1L
                it.sectionType shouldBe PostSectionType.TEXT
                it.data shouldBe TextPostSectionContent(
                    content = section1.content,
                    extra = emptyMap(),
                ).toJson()
            }
            postSections[1].also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.parentId shouldBe ""
                it.key.slotId shouldBe 1L
                it.key.priority shouldBe 2L
                it.sectionType shouldBe PostSectionType.TEXT
                it.data shouldBe TextPostSectionContent(
                    content = section2.content,
                    extra = emptyMap(),
                ).toJson()
            }
        }

        test("포스트에 하위 포스트가 생성되면, 하위 포스트가 존재한다고 메타데이터가 추가된다") {
            // given
            val postSpaceKey = PostSpaceKey(
                workspaceId = "story",
                componentId = "post",
                spaceId = "commentId",
            )

            val parentPost = PostFixture.create(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                spaceId = postSpaceKey.spaceId,
                parentId = null,
            )
            val parentPostReverse = PostReverse.of(parentPost)

            postRepository.save(parentPost)
            postReverseRepository.save(parentPostReverse)

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
                parentId = parentPost.key.toPostKey(),
                ownerId = ownerId,
                title = title,
                sections = listOf(section1, section2),
            )

            // then
            val findParentPost = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
                workspaceId = parentPost.key.workspaceId,
                componentId = parentPost.key.componentId,
                spaceId = parentPost.key.spaceId,
                parentId = parentPost.key.parentId,
                slotId = parentPost.key.slotId,
                postId = parentPost.key.postId,
            )
            findParentPost shouldNotBe null
            findParentPost!!.also {
                it.key.workspaceId shouldBe postSpaceKey.workspaceId
                it.key.componentId shouldBe postSpaceKey.componentId
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.parentId shouldBe ""
                it.key.slotId shouldBe parentPost.key.slotId
                it.key.postId shouldBe parentPost.key.postId
                it.ownerId shouldBe parentPost.ownerId
                it.title shouldBe parentPost.title
                it.metadata shouldContainExactly mapOf(PostMetadataType.HAS_CHILDREN to true.toString())
            }
        }

        test("포스트의 하위에 포스트를 추가할때, 해당 부모 포스트가 존재하지 않는 경우 등록에 실패한다") {
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

            // when & then
            shouldThrowExactly<ParentPostNotExistsException> {
                postCreator.createPost(
                    postSpaceKey = postSpaceKey,
                    parentId = PostKey(spaceId = postSpaceKey.spaceId, depth = 1, postId = 1, parentId = null),
                    ownerId = ownerId,
                    title = title,
                    sections = listOf(section1, section2),
                )
            }
        }
    }

})
