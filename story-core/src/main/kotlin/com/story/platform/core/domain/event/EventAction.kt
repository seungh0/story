package com.story.platform.core.domain.event

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.domain.resource.ResourceId

enum class EventAction(
    private val description: String,
    private val resourceIds: Set<ResourceId>,
) {

    CREATED(description = "생성", resourceIds = setOf(ResourceId.POSTS, ResourceId.SUBSCRIPTIONS)),
    UPDATED(description = "수정", resourceIds = setOf(ResourceId.POSTS)),
    DELETED(description = "삭제", resourceIds = setOf(ResourceId.POSTS, ResourceId.SUBSCRIPTIONS)),
    ;

    fun validateActionForResource(resourceId: ResourceId) {
        if (!this.resourceIds.contains(resourceId)) {
            throw NotSupportedException("리소스($resourceId)에서 사용할 수 없는 EventAction(${this.name})입니다")
        }
    }

    companion object {
        private val resourceIdEventActionsMap = mutableMapOf<ResourceId, MutableSet<EventAction>>()

        init {
            resourceIdEventActionsMap += values().flatMap { eventAction ->
                eventAction.resourceIds.map { resourceId ->
                    resourceId to eventAction
                }
            }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, eventActions) -> eventActions.toMutableSet() }
        }

        fun getAvailableActions(resourceId: ResourceId) = resourceIdEventActionsMap[resourceId] ?: emptySet()
    }

}
