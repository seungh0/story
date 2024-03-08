package com.story.worker.application.feed

import com.story.core.domain.event.EventAction
import com.story.worker.IntegrationTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf

@IntegrationTest
class FeedFanoutHandlerFinderTest(
    private val feedHandlerFinder: FeedItemFanoutActionHandlerBeanFinder,
) : StringSpec({

    "Feed Create Handler" {
        // given
        val eventAction = EventAction.CREATED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<FeedItemFanoutCreateActionHandler>()
    }

    "Feed Remove Handler" {
        // given
        val eventAction = EventAction.REMOVED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<FeedItemFanoutRemoveActionHandler>()
    }

})
