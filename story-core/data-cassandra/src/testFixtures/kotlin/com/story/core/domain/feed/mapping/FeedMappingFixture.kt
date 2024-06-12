package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.RandomGenerator
import java.time.Duration

object FeedMappingFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        feedComponentId: String = RandomGenerator.generateString(),
        sourceResourceId: ResourceId = RandomGenerator.generateEnum(ResourceId::class.java),
        sourceComponentId: String = RandomGenerator.generateString(),
        subscriptionComponentId: String = RandomGenerator.generateString(),
        description: String = RandomGenerator.generateString(),
        retention: Duration = Duration.ofDays(30),
    ) = FeedMappingEntity.of(
        workspaceId = workspaceId,
        feedComponentId = feedComponentId,
        sourceResourceId = sourceResourceId,
        sourceComponentId = sourceComponentId,
        subscriptionComponentId = subscriptionComponentId,
        description = description,
        retention = retention,
    )

}