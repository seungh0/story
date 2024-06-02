package com.story.core.domain.feed.mapping

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import com.story.core.domain.resource.ResourceId
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import java.time.Duration

@IntegrationTest
class FeedMappingCreatorTest(
    private val feedMappingCreator: FeedMappingCreator,
    private val feedMappingCassandraRepository: FeedMappingCassandraRepository,
    private val feedMappingReverseCassandraRepository: FeedMappingReverseCassandraRepository,
) : StringSpecIntegrationTest({

    "FeedMapping을 추가합니다" {
        // given
        val request = FeedMappingCreateCommand(
            workspaceId = "workspaceId",
            feedComponentId = "timeline",
            sourceResourceId = ResourceId.POSTS,
            sourceComponentId = "home",
            subscriptionComponentId = "follow",
            description = "포스트 등록시 팔로워들에게 타임라인 피드 발행",
            retention = Duration.ofDays(30),
        )

        // when
        feedMappingCreator.create(request)

        // then
        val feedMappings = feedMappingCassandraRepository.findAll().toList()
        feedMappings shouldHaveSize 1
        feedMappings[0].also {
            it.key.workspaceId shouldBe request.workspaceId
            it.key.feedComponentId shouldBe request.feedComponentId
            it.key.sourceResourceId shouldBe request.sourceResourceId
            it.key.sourceComponentId shouldBe request.sourceComponentId
            it.key.subscriptionComponentId shouldBe request.subscriptionComponentId
            it.description shouldBe request.description
            it.retention shouldBe request.retention
            it.auditingTime.createdAt shouldNotBe null
            it.auditingTime.updatedAt shouldBe it.auditingTime.createdAt
        }

        val feedMappingReverses = feedMappingReverseCassandraRepository.findAll().toList()
        feedMappingReverses shouldHaveSize 1
        feedMappingReverses[0].also {
            it.key.workspaceId shouldBe request.workspaceId
            it.key.feedComponentId shouldBe request.feedComponentId
            it.key.sourceResourceId shouldBe request.sourceResourceId
            it.key.sourceComponentId shouldBe request.sourceComponentId
            it.key.subscriptionComponentId shouldBe request.subscriptionComponentId
            it.retention shouldBe request.retention
        }
    }

    "이미 설정된 피드 매핑인 경우 등록할 수 없다" {
        // given
        val workspaceId = "workspaceId"
        val feedComponentId = "timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "home"
        val subscriptionComponentId = "follow"

        val feedMapping = FeedMappingFixture.create(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
        )
        feedMappingCassandraRepository.save(feedMapping)

        val request = FeedMappingCreateCommand(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
            description = "포스트 등록시 팔로워들에게 타임라인 피드 발행",
            retention = Duration.ofDays(30),
        )

        // when & then
        shouldThrowExactly<FeedMappingAlreadyConnectedException> {
            feedMappingCreator.create(request)
        }
    }

    "Source - Subscription 관계간 최대 3개까지만 피드 매핑을 설정할 수 있다" {
        // given
        val workspaceId = "workspaceId"
        val feedComponentId = "timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "home"

        val feedMappings = (1..3).map {
            FeedMappingFixture.create(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                subscriptionComponentId = it.toString()
            )
        }
        feedMappingCassandraRepository.saveAll(feedMappings).toList()

        val request = FeedMappingCreateCommand(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = "4",
            description = "포스트 등록시 팔로워들에게 타임라인 피드 발행",
            retention = Duration.ofDays(30),
        )

        // when & then
        shouldThrowExactly<FeedMappingCapacityExceededException> {
            feedMappingCreator.create(request)
        }
    }

})
