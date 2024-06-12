package com.story.core.domain.purge

import com.story.core.common.error.NotSupportedException
import com.story.core.domain.resource.ResourceId
import com.story.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class PurgerBeanFinder(
    private val springBeanProvider: SpringBeanProvider,
) : PurgerFinder {

    override operator fun get(resourceId: ResourceId) = resourceIdPurgerMap[resourceId]
        ?: throw NotSupportedException("Resource($resourceId)에 해당하는 Purger는 지원하지 않습니다")

    @PostConstruct
    fun initialize() {
        resourceIdPurgerMap += springBeanProvider.convertBeanMap(
            Purger::class.java,
            Purger::targetResourceId,
        )
    }

    companion object {
        private val resourceIdPurgerMap = EnumMap<ResourceId, Purger>(ResourceId::class.java)
    }

}
