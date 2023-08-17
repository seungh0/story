package com.story.platform.executor.domain.feed

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.common.spring.SpringBeanProvider
import com.story.platform.core.domain.event.EventAction
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class FeedHandlerFinder(
    private val springBeanProvider: SpringBeanProvider,
) {

    operator fun get(eventAction: EventAction) = eventActionFeedPublisherMap[eventAction]
        ?: throw NotSupportedException("EventAction($eventAction)에 해당하는 FeedPublisher는 지원하지 않습니다")

    @PostConstruct
    fun initialize() {
        eventActionFeedPublisherMap += springBeanProvider.convertBeanMap(
            FeedPublisher::class.java,
            FeedPublisher::targetEventAction,
        )
    }

    companion object {
        private val eventActionFeedPublisherMap = EnumMap<EventAction, FeedPublisher>(EventAction::class.java)
    }

}
