package com.story.core.domain.post.section

import com.story.core.common.error.NotSupportedException
import com.story.core.infrastructure.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class PostSectionHandlerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : PostSectionHandlerFinder {

    override operator fun get(sectionType: PostSectionType) = sectionHandlerEnumMap[sectionType]
        ?: throw NotSupportedException("Section($sectionType)에 해당하는 PostSectionHandler는 지원하지 않습니다")

    @PostConstruct
    fun initialize() {
        sectionHandlerEnumMap += springBeanProvider.convertBeanMap(
            PostSectionHandler::class.java,
            PostSectionHandler::sectionType,
        )
    }

    companion object {
        private val sectionHandlerEnumMap = EnumMap<PostSectionType, PostSectionHandler>(PostSectionType::class.java)
    }

}
