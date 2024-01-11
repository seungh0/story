package com.story.core.domain.reaction

import com.story.core.support.RandomGenerator

object ReactionFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        componentId: String = RandomGenerator.generateString(),
        spaceId: String = RandomGenerator.generateString(),
        accountId: String = RandomGenerator.generateString(),
        optionIds: Set<String> = setOf(RandomGenerator.generateString(), RandomGenerator.generateString()),
    ) = Reaction.of(
        workspaceId = workspaceId,
        componentId = componentId,
        spaceId = spaceId,
        accountId = accountId,
        emotionIds = optionIds,
    )

}
