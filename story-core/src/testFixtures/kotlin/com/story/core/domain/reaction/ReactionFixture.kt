package com.story.core.domain.reaction

import com.story.core.support.RandomGenerator

object ReactionFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        componentId: String = RandomGenerator.generateString(),
        spaceId: String = RandomGenerator.generateString(),
        userId: String = RandomGenerator.generateString(),
        optionIds: Set<String> = setOf(RandomGenerator.generateString(), RandomGenerator.generateString()),
    ) = ReactionEntity.of(
        workspaceId = workspaceId,
        componentId = componentId,
        spaceId = spaceId,
        userId = userId,
        emotionIds = optionIds,
    )

}
