package com.story.platform.publisher.application.feed

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.infrastructure.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class FeedHandlerFinder(
    private val springBeanProvider: SpringBeanProvider,
) {

    operator fun get(eventAction: EventAction) = eventActionFeedHandlerMap[eventAction]
        ?: throw NotSupportedException("EventAction($eventAction)에 해당하는 FeedHandler는 지원하지 않습니다")

    @PostConstruct
    fun initialize() {
        eventActionFeedHandlerMap += springBeanProvider.convertBeanMap(
            FeedHandler::class.java,
            FeedHandler::targetEventAction,
        )
    }

    companion object {
        private val eventActionFeedHandlerMap = EnumMap<EventAction, FeedHandler>(EventAction::class.java)
    }

}
