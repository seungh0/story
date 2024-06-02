package com.story.core.domain.reaction

import com.story.core.domain.event.EventKey

data class ReactionEventKey(
    val spaceId: String,
    val userId: String,
) : EventKey {

    override fun makeKey(): String = "reaction::$spaceId::$userId"

}
