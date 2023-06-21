package com.story.platform.core.domain.feed.source

import com.story.platform.core.common.error.NotImplementedException
import com.story.platform.core.domain.feed.FeedSourceType
import com.story.platform.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class FeedSourceImporterFinder(
    private val springBeanProvider: SpringBeanProvider,
) {

    fun findImporter(feedSourceType: FeedSourceType): FeedSourceImporter {
        return feedSourcesMap[feedSourceType]
            ?: throw NotImplementedException("피드 SourceType($feedSourceType)에 등록된 Importer가 존재하지 않습니다")
    }

    @PostConstruct
    fun initialize() {
        feedSourcesMap.putAll(
            springBeanProvider.convertBeanMap(
                FeedSourceImporter::class.java,
                FeedSourceImporter::sourceType,
            )
        )
    }

    companion object {
        private val feedSourcesMap = EnumMap<FeedSourceType, FeedSourceImporter>(FeedSourceType::class.java)
    }

}
