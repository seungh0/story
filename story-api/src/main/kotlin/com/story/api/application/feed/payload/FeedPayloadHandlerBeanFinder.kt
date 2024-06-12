package com.story.api.application.feed.payload

import com.story.core.common.error.NotSupportedException
import com.story.core.domain.resource.ResourceId
import com.story.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class FeedPayloadHandlerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : FeedPayloadHandlerFinder {

    override operator fun get(resourceId: ResourceId) = resourceIdPurgerMap[resourceId]
        ?: throw NotSupportedException("Resource($resourceId)에 해당하는 FeedPayloadHandler는 지원하지 않습니다")

    @PostConstruct
    fun initialize() {
        resourceIdPurgerMap += springBeanProvider.convertBeanMap(
            FeedPayloadHandler::class.java,
            FeedPayloadHandler::resourceId,
        )
    }

    companion object {
        private val resourceIdPurgerMap = EnumMap<ResourceId, FeedPayloadHandler>(ResourceId::class.java)
    }

}
