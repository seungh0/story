package com.story.platform.core.domain.post

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.story.platform.core.LoadCqlScriptsHelper
import com.story.platform.core.common.enums.ServiceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate

@SpringBootTest
internal class PostRegisterTest(
    private val postRegister: PostRegister,
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

    context("포스트 등록") {
        test("신규 포스트를 등록합니다") {
            // given
            val postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = PostSpaceType.ACCOUNT,
                spaceId = "commentId",
            )
            val accountId = "accountId"
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

            // when
            postRegister.register(
                postSpaceKey = postSpaceKey,
                accountId = accountId,
                title = title,
                content = content,
                extraJson = extraJson,
            )

            // then
            val posts = postCoroutineRepository.findAll().toList()
            posts shouldHaveSize 1
            posts[0].also {
                it.key.serviceType shouldBe postSpaceKey.serviceType
                it.key.spaceType shouldBe postSpaceKey.spaceType
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.slotId shouldBe 1L
                it.key.postId shouldNotBe null
                it.accountId shouldBe accountId
                it.title shouldBe title
                it.content shouldBe content
                it.extraJson shouldBe extraJson
            }

            val postReverses = postReverseCoroutineRepository.findAll().toList()
            postReverses shouldHaveSize 1
            postReverses[0].also {
                it.key.serviceType shouldBe postSpaceKey.serviceType
                it.key.accountId shouldBe accountId
                it.key.spaceType shouldBe postSpaceKey.spaceType
                it.key.spaceId shouldBe postSpaceKey.spaceId
                it.key.postId shouldNotBe null
                it.title shouldBe title
                it.content shouldBe content
                it.extraJson shouldBe extraJson
            }
        }
    }

})
