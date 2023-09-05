package com.story.platform.core.domain.reaction

import com.story.platform.core.support.RandomGenerator

object ReactionFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        componentId: String = RandomGenerator.generateString(),
        targetId: String = RandomGenerator.generateString(),
        accountId: String = RandomGenerator.generateString(),
        optionIds: Set<String> = setOf(RandomGenerator.generateString(), RandomGenerator.generateString()),
    ) = Reaction.of(
        workspaceId = workspaceId,
        componentId = componentId,
        targetId = targetId,
        accountId = accountId,
        optionIds = optionIds,
    )

}
