package com.story.core.domain.purge

import com.story.core.domain.resource.ResourceId

fun interface PurgerFinder {

    operator fun get(resourceId: ResourceId): Purger

}
