package com.story.worker.application.feed

import com.story.core.common.error.NotSupportedException
import com.story.core.domain.event.EventAction
import com.story.core.infrastructure.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class FeedFanoutHandlerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : FeedFanoutHandlerFinder {

    override operator fun get(eventAction: EventAction) = eventActionFeedHandlerMap[eventAction]
        ?: throw NotSupportedException("EventAction($eventAction)에 해당하는 FeedHandler는 지원하지 않습니다")

    @PostConstruct
    fun initialize() {
        eventActionFeedHandlerMap += springBeanProvider.convertBeanMap(
            FeedFanoutHandler::class.java,
            FeedFanoutHandler::targetEventAction,
        )
    }

    companion object {
        private val eventActionFeedHandlerMap = EnumMap<EventAction, FeedFanoutHandler>(EventAction::class.java)
    }

}
