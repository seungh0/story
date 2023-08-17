package com.story.platform.executor.domain.feed

import com.story.platform.core.domain.event.EventAction
import com.story.platform.executor.IntegrationTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf

@IntegrationTest
class FeedHandlerFinderTest(
    private val feedHandlerFinder: FeedHandlerFinder,
) : StringSpec({

    "Feed Create Handler" {
        // given
        val eventAction = EventAction.CREATED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<FeedCreateHandler>()
    }

    "Feed Updated Handler" {
        // given
        val eventAction = EventAction.UPDATED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<FeedModifyHandler>()
    }

    "Feed Remove Handler" {
        // given
        val eventAction = EventAction.DELETED

        // when
        val sut = feedHandlerFinder.get(eventAction = eventAction)

        // then
        sut should beInstanceOf<FeedRemoveHandler>()
    }

})
