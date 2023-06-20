package com.story.platform.core.domain.post

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.error.ForbiddenException
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.helper.TestCleaner
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostModifierTest(
    private val postModifier: PostModifier,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("등록된 포스트를 수정한다") {
        test("기존에 등록된 포스트를 수정합니다") {
            // given
            val title = "포스트 제목"
            val content = """
                포스트 내용
                입니다
            """.trimIndent()
            val extraJson = """
                {
                  "option": false
                }
            """.trimIndent()

            val post = PostFixture.create()
            val postRev = PostReverse.of(post)

            postRepository.save(post)
            postReverseRepository.save(postRev)

            // when
            postModifier.patch(
                postSpaceKey = PostSpaceKey(
                    workspaceId = post.key.workspaceId,
                    spaceType = post.key.spaceType,
                    spaceId = post.key.spaceId,
                ),
                postId = post.key.postId,
                accountId = post.accountId,
                title = title,
                content = content,
                extraJson = extraJson,
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.spaceType shouldBe post.key.spaceType
                it.key.spaceId shouldBe post.key.spaceId
                it.key.slotId shouldBe post.key.slotId
                it.key.postId shouldBe post.key.postId
                it.accountId shouldBe post.accountId
                it.title shouldBe title
                it.content shouldBe content
                it.extraJson shouldBe extraJson
            }

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 1
            postReverses[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.accountId shouldBe post.accountId
                it.key.spaceType shouldBe post.key.spaceType
                it.key.spaceId shouldBe post.key.spaceId
                it.key.postId shouldBe post.key.postId
                it.title shouldBe title
                it.content shouldBe content
                it.extraJson shouldBe extraJson
            }
        }

        test("존재하지 않는 포스트의 경우 포스트 정보를 수정할 수 없습니다") {
            // given
            val title = "포스트 제목"
            val content = "포스트 내용"
            val extraJson = null

            // when & then
            shouldThrowExactly<NotFoundException> {
                postModifier.patch(
                    postSpaceKey = PostSpaceKey(
                        workspaceId = "twitter",
                        spaceType = PostSpaceType.ACCOUNT,
                        spaceId = "50000",
                    ),
                    postId = 10000L,
                    accountId = "accountId",
                    title = title,
                    content = content,
                    extraJson = extraJson,
                )
            }
        }

        test("포스트의 작성자만이 포스트를 수정할 수 있습니다") {
            // given
            val title = "포스트 제목"
            val content = "포스트 내용"
            val extraJson = null

            val post = PostFixture.create(accountId = "accountId")
            val postRev = PostReverse.of(post)

            postRepository.save(post)
            postReverseRepository.save(postRev)

            // when & then
            shouldThrowExactly<ForbiddenException> {
                postModifier.patch(
                    postSpaceKey = PostSpaceKey(
                        workspaceId = post.key.workspaceId,
                        spaceType = post.key.spaceType,
                        spaceId = post.key.spaceId,
                    ),
                    postId = post.key.postId,
                    accountId = "another Account Id",
                    title = title,
                    content = content,
                    extraJson = extraJson,
                )
            }
        }
    }

})
