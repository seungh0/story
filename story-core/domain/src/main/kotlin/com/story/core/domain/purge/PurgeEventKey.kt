package com.story.core.domain.purge

import com.story.core.domain.event.EventKey
import com.story.core.domain.resource.ResourceId

data class PurgeEventKey(
    val resourceId: ResourceId,
    val componentId: String,
) : EventKey {

    override fun makeKey(): String = "purge::${resourceId.code}::$componentId"

}
