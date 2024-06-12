package com.story.distributor.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class PostFeedActionHandlerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : PostFeedActionHandlerFinder {

    override operator fun get(eventAction: EventAction): PostFeedEventActionHandler? =
        postFeedDistributeEventHandlers[eventAction]

    @PostConstruct
    fun initialize() {
        postFeedDistributeEventHandlers += springBeanProvider.convertBeanMap(
            PostFeedEventActionHandler::class.java,
            PostFeedEventActionHandler::eventAction,
        )
    }

    companion object {
        private val postFeedDistributeEventHandlers = EnumMap<EventAction, PostFeedEventActionHandler>(EventAction::class.java)
    }

}
