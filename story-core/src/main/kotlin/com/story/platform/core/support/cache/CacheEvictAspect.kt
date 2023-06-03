package com.story.platform.core.support.cache

import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.support.coroutine.runCoroutine
import com.story.platform.core.support.spring.SpringExpressionParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class CacheEvictAspect(
    private val cacheManager: CacheManager,
) {

    @Around("args(.., kotlin.coroutines.Continuation) && @annotation(CacheEvict)")
    fun evict(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val cacheEvict = method.getAnnotation(CacheEvict::class.java)

        val value = joinPoint.proceed(joinPoint.args)

        val cacheType = cacheEvict.cacheType

        return joinPoint.runCoroutine {
            if (cacheEvict.allEntries) {
                cacheManager.evictAllCachesLayeredCache(cacheType = cacheType)
                return@runCoroutine value
            }

            val cacheKeyString = SpringExpressionParser.parseString(
                parameterNames = methodSignature.parameterNames,
                args = joinPoint.args,
                key = cacheEvict.key,
            )

            if (cacheKeyString.isNullOrBlank()) {
                throw InternalServerException("@CacheEvict key can't be null. [cacheType: ${cacheEvict.cacheType} parameterNames: ${methodSignature.parameterNames} args: ${joinPoint.args} key: ${cacheEvict.key}]")
            }

            cacheManager.evictCacheLayeredCache(cacheType = cacheType, cacheKey = cacheKeyString)

            return@runCoroutine value
        }
    }

}
