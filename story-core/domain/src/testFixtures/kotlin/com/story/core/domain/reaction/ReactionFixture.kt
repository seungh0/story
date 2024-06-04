package com.story.core.domain.reaction

import com.story.core.support.RandomGenerator.generateString

object ReactionFixture {

    fun create(
        workspaceId: String = generateString(),
        componentId: String = generateString(),
        spaceId: String = generateString(),
        emotionIds: Set<String> = setOf(
            generateString(),
            generateString()
        ),
    ) = Reaction(
        workspaceId = workspaceId,
        componentId = componentId,
        spaceId = spaceId,
        emotionIds = emotionIds
    )

}
