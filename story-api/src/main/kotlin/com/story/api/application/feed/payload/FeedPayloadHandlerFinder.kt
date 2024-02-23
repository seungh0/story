package com.story.api.application.feed.payload

import com.story.core.domain.resource.ResourceId

fun interface FeedPayloadHandlerFinder {

    operator fun get(resourceId: ResourceId): FeedPayloadHandler

}
