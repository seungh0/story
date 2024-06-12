package com.story.worker.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class FeedItemFanoutActionHandlerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : FeedItemFanoutActionHandlerFinder {

    override operator fun get(eventAction: EventAction) = feedItemFanoutActionHandlers[eventAction]

    @PostConstruct
    fun initialize() {
        feedItemFanoutActionHandlers += springBeanProvider.convertBeanMap(
            FeedItemFanoutActionHandler::class.java,
            FeedItemFanoutActionHandler::eventAction,
        )
    }

    companion object {
        private val feedItemFanoutActionHandlers = EnumMap<EventAction, FeedItemFanoutActionHandler>(EventAction::class.java)
    }

}
