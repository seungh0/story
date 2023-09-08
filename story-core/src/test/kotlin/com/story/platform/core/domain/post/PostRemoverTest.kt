package com.story.platform.core.domain.post

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostRemoverTest(
    private val postRemover: PostRemover,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("등록된 포스트를 삭제한다") {
        test("기존에 등록된 포스트를 삭제한다") {
            // given
            val post = PostFixture.create()
            postRepository.save(post)

            val postReverse = PostReverse.of(post)
            postReverseRepository.save(postReverse)

            // when
            postRemover.remove(
                postSpaceKey = PostSpaceKey(
                    workspaceId = post.key.workspaceId,
                    componentId = post.key.componentId,
                    spaceId = post.key.spaceId,
                ),
                postId = post.key.postId,
                accountId = post.accountId,
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 0

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 0
        }

        test("등록되지 않은 포스트를 삭제하는 경우 멱등성을 갖는다") {
            // when
            postRemover.remove(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "twitter",
                    componentId = "post",
                    spaceId = "포스트 작성 공간 ID",
                ),
                postId = -1L,
                accountId = "포스트 작성자 ID",
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 0

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 0
        }

        test("다른 계정이 작성한 포스트를 삭제할 수 없다") {
            // given
            val post = PostFixture.create(accountId = "account-1")
            postRepository.save(post)

            val postReverse = PostReverse.of(post)
            postReverseRepository.save(postReverse)

            // when
            postRemover.remove(
                postSpaceKey = PostSpaceKey(
                    workspaceId = post.key.workspaceId,
                    componentId = post.key.componentId,
                    spaceId = post.key.spaceId,
                ),
                postId = post.key.postId,
                accountId = "account-2",
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.componentId shouldBe post.key.componentId
                it.key.spaceId shouldBe post.key.spaceId
                it.key.slotId shouldBe 1L
                it.key.postId shouldBe post.key.postId
                it.accountId shouldBe post.accountId
                it.title shouldBe post.title
                it.content shouldBe post.content
                it.extra shouldBe post.extra
            }

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 1
            postReverses[0].also {
                it.key.workspaceId shouldBe post.key.workspaceId
                it.key.componentId shouldBe post.key.componentId
                it.key.accountId shouldBe post.accountId
                it.key.spaceId shouldBe post.key.spaceId
                it.key.postId shouldBe post.key.postId
                it.title shouldBe post.title
                it.content shouldBe post.content
                it.extra shouldBe post.extra
            }
        }
    }

})
