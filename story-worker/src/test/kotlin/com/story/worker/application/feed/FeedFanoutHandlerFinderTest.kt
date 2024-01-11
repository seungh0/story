package com.story.worker.application.feed

import com.story.core.domain.event.EventAction
import com.story.worker.IntegrationTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf

@IntegrationTest
class FeedFanoutHandlerFinderTest(
    private val feedHandlerFinder: FeedFanoutHandlerFinder,
) : StringSpec({

    "Feed Create Handler" {
        // given
        val eventAction = EventAction.CREATED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<CreateFeedFanoutHandler>()
    }

    "Feed Updated Handler" {
        // given
        val eventAction = EventAction.UPDATED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<ModifyFeedFanoutHandler>()
    }

    "Feed Remove Handler" {
        // given
        val eventAction = EventAction.DELETED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<RemoveFeedFanoutHandler>()
    }

})
