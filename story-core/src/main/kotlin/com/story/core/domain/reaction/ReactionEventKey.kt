package com.story.core.domain.reaction

import com.story.core.domain.event.EventKey

data class ReactionEventKey(
    val spaceId: String,
    val accountId: String,
) : EventKey {

    override fun makeKey(): String = "reaction::$spaceId::$accountId"

}
