package com.story.distributor.application.feed

import com.story.core.domain.event.EventAction
import com.story.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class SubscriptionFeedHandlerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : SubscriptionFeedHandlerFinder {

    override operator fun get(eventAction: EventAction): SubscriptionFeedActionHandler? =
        subscriptionFeedDistributeEventHandlers[eventAction]

    @PostConstruct
    fun initialize() {
        subscriptionFeedDistributeEventHandlers += springBeanProvider.convertBeanMap(
            SubscriptionFeedActionHandler::class.java,
            SubscriptionFeedActionHandler::eventAction,
        )
    }

    companion object {
        private val subscriptionFeedDistributeEventHandlers = EnumMap<EventAction, SubscriptionFeedActionHandler>(
            EventAction::class.java
        )
    }

}
