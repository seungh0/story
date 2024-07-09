package com.story.core.domain.post

import com.story.core.FunSpecIntegrationTest
import com.story.core.IntegrationTest
import com.story.core.common.error.NoPermissionException
import com.story.core.common.json.toJson
import com.story.core.domain.post.section.PostSectionCassandraRepository
import com.story.core.domain.post.section.PostSectionSlotAssigner
import com.story.core.domain.post.section.PostSectionType
import com.story.core.domain.post.section.text.TextPostSectionContentCommand
import com.story.core.domain.post.section.text.TextPostSectionContentEntity
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostModifierTest(
    private val postModifier: PostModifier,
    private val postRepository: PostCassandraRepository,
    private val postReverseCassandraRepository: PostReverseCassandraRepository,
    private val postSectionCassandraRepository: PostSectionCassandraRepository,
) : FunSpecIntegrationTest({

    context("등록된 포스트를 수정한다") {
        test("기존에 등록된 포스트를 수정합니다") {
            // given
            val title = "포스트 제목"

            val post = PostFixture.create()
            val postRev = PostReverse.of(post)

            postRepository.save(post)
            postReverseCassandraRepository.save(postRev)

            val section1 = TextPostSectionContentCommand(
                content = "컨텐츠 내용 - 1",
                priority = 1L,
            )
            val section2 = TextPostSectionContentCommand(
                content = "컨텐츠 내용 - 2",
                priority = 2L,
            )
            val extra = mapOf(
                "commentEnabled" to "true"
            )

            // when
            postModifier.patchPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = post.key.workspaceId,
                    componentId = post.key.componentId,
                    spaceId = post.key.spaceId,
                ),
                postId = PostId(spaceId = post.key.spaceId, depth = 1, parentId = null, postNo = post.key.postNo),
                ownerId = post.ownerId,
                title = title,
                sections = listOf(section1, section2),
                extra = extra,
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.componentId shouldBe post.key.componentId
                it.key.spaceId shouldBe post.key.spaceId
                it.key.slotId shouldBe post.key.slotId
                it.key.postNo shouldBe post.key.postNo
                it.ownerId shouldBe post.ownerId
                it.title shouldBe title
                it.extra shouldBe extra
            }

            val postReverses = postReverseCassandraRepository.findAll().toList()
            postReverses shouldHaveSize 1
            postReverses[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.ownerId shouldBe post.ownerId
                it.key.spaceId shouldBe post.key.spaceId
                it.key.postNo shouldBe post.key.postNo
                it.title shouldBe title
            }

            val postSections = postSectionCassandraRepository.findAll().toList()
            postSections shouldHaveSize 2
            postSections[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.componentId shouldBe post.key.componentId
                it.key.spaceId shouldBe post.key.spaceId
                it.key.slotId shouldBe PostSectionSlotAssigner.assign(postId = post.key.postNo)
                it.key.priority shouldBe 1L
                it.sectionType shouldBe PostSectionType.TEXT
                it.data shouldBe TextPostSectionContentEntity(
                    content = section1.content,
                    extra = emptyMap(),
                ).toJson()
            }
            postSections[1].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.componentId shouldBe post.key.componentId
                it.key.spaceId shouldBe post.key.spaceId
                it.key.slotId shouldBe PostSectionSlotAssigner.assign(postId = post.key.postNo)
                it.key.priority shouldBe 2L
                it.sectionType shouldBe PostSectionType.TEXT
                it.data shouldBe TextPostSectionContentEntity(
                    content = section2.content,
                    extra = emptyMap(),
                ).toJson()
            }
        }

        test("존재하지 않는 포스트의 경우 포스트 정보를 수정할 수 없습니다") {
            // given
            val title = "포스트 제목"

            // when & then
            shouldThrowExactly<PostNotExistsException> {
                postModifier.patchPost(
                    postSpaceKey = PostSpaceKey(
                        workspaceId = "story",
                        componentId = "post",
                        spaceId = "50000",
                    ),
                    postId = PostId(spaceId = "50000", depth = 1, parentId = null, postNo = 1000),
                    ownerId = "user-1",
                    title = title,
                    sections = emptyList(),
                    extra = emptyMap(),
                )
            }
        }

        test("포스트의 작성자만이 포스트를 수정할 수 있습니다") {
            // given
            val title = "포스트 제목"

            val post = PostFixture.create(ownerId = "user-10")
            val postRev = PostReverse.of(post)

            postRepository.save(post)
            postReverseCassandraRepository.save(postRev)

            // when & then
            shouldThrowExactly<NoPermissionException> {
                postModifier.patchPost(
                    postSpaceKey = PostSpaceKey(
                        workspaceId = post.key.workspaceId,
                        componentId = post.key.componentId,
                        spaceId = post.key.spaceId,
                    ),
                    postId = PostId(spaceId = post.key.spaceId, depth = 1, parentId = null, postNo = post.key.postNo),
                    ownerId = "another Owner Id",
                    title = title,
                    sections = emptyList(),
                    extra = emptyMap(),
                )
            }
        }
    }

    context("포스트의 메타 정보를 수정한다") {

        test("포스트의 하위 댓글 존재 여부에 대한 메타 정보를 수정한다") {
            // given
            val post = PostFixture.create()
            val postRev = PostReverse.of(post)

            postRepository.save(post)
            postReverseCassandraRepository.save(postRev)

            // when
            postModifier.putMetadata(
                postSpaceKey = PostSpaceKey(
                    workspaceId = post.key.workspaceId,
                    componentId = post.key.componentId,
                    spaceId = post.key.spaceId,
                ),
                postId = PostId(spaceId = post.key.spaceId, depth = 1, parentId = null, postNo = post.key.postNo),
                metadataType = PostMetadataType.HAS_CHILDREN,
                value = true,
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].key shouldBe post.key
            posts[0].metadata shouldBe mutableMapOf(PostMetadataType.HAS_CHILDREN to true.toString())
        }

        test("포스트 메타데이터 수정시 포스트가 존재하지 않는 경우 실패한다") {
            // given
            val post = PostFixture.create()

            // when & then
            shouldThrowExactly<PostNotExistsException> {
                postModifier.putMetadata(
                    postSpaceKey = PostSpaceKey(
                        workspaceId = post.key.workspaceId,
                        componentId = post.key.componentId,
                        spaceId = post.key.spaceId,
                    ),
                    postId = PostId(spaceId = post.key.spaceId, depth = 1, parentId = null, postNo = post.key.postNo),
                    metadataType = PostMetadataType.HAS_CHILDREN,
                    value = true,
                )
            }
        }

    }

})