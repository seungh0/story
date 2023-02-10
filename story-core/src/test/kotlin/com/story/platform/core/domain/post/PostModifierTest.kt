package com.story.platform.core.domain.post

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.story.platform.core.LoadCqlScriptsHelper
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.ForbiddenException
import com.story.platform.core.common.error.NotFoundException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate

@SpringBootTest
internal class PostModifierTest(
    private val postModifier: PostModifier,
    private val postCoroutineRepository: PostCoroutineRepository,
    private val postReverseCoroutineRepository: PostReverseCoroutineRepository,
    private val reactiveCassandraTemplate: ReactiveCassandraTemplate,
) : FunSpec({

    beforeEach {
        reactiveCassandraTemplate.execute(SimpleStatement.newInstance(LoadCqlScriptsHelper.POST_REVERSE_V1)).subscribe()
    }

    afterEach {
        postCoroutineRepository.deleteAll()
        postReverseCoroutineRepository.deleteAll()
    }

    context("포스트 수정") {
        test("등록된 포스트를 수정합니다") {
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

            postCoroutineRepository.save(post)
            postReverseCoroutineRepository.save(postRev)

            // when
            postModifier.modify(
                postSpaceKey = PostSpaceKey(
                    serviceType = post.key.serviceType,
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
            val posts = postCoroutineRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].also {
                it.key.serviceType shouldBe post.key.serviceType
                it.key.spaceType shouldBe post.key.spaceType
                it.key.spaceId shouldBe post.key.spaceId
                it.key.slotId shouldBe post.key.slotId
                it.key.postId shouldBe post.key.postId
                it.accountId shouldBe post.accountId
                it.title shouldBe title
                it.content shouldBe content
                it.extraJson shouldBe extraJson
            }

            val postReverses = postReverseCoroutineRepository.findAll().toList()
            postReverses shouldHaveSize 1
            postReverses[0].also {
                it.key.serviceType shouldBe post.key.serviceType
                it.key.accountId shouldBe post.accountId
                it.key.spaceType shouldBe post.key.spaceType
                it.key.spaceId shouldBe post.key.spaceId
                it.key.postId shouldBe post.key.postId
                it.title shouldBe title
                it.content shouldBe content
                it.extraJson shouldBe extraJson
            }
        }

        test("존재하지 않는 포스트를 수정할 수 없다") {
            // given
            val title = "포스트 제목"
            val content = "포스트 내용"
            val extraJson = null

            // when & then
            shouldThrowExactly<NotFoundException> {
                postModifier.modify(
                    postSpaceKey = PostSpaceKey(
                        serviceType = ServiceType.TWEETER,
                        spaceType = "comment",
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

        test("해당 포스트의 작성자가 아닌 경우 포스트를 수정할수 없다") {
            // given
            val title = "포스트 제목"
            val content = "포스트 내용"
            val extraJson = null

            val post = PostFixture.create(accountId = "accountId")
            val postRev = PostReverse.of(post)

            postCoroutineRepository.save(post)
            postReverseCoroutineRepository.save(postRev)

            // when & then
            shouldThrowExactly<ForbiddenException> {
                postModifier.modify(
                    postSpaceKey = PostSpaceKey(
                        serviceType = post.key.serviceType,
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
