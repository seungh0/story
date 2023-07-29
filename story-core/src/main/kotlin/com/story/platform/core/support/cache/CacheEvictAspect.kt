package com.story.platform.core.support.cache

import com.story.platform.core.common.coroutine.runCoroutine
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.common.spring.SpringExpressionParser
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

        if (!precondition(joinPoint = joinPoint, methodSignature = methodSignature, cacheEvict = cacheEvict)) {
            return joinPoint.proceed(joinPoint.args)
        }

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

            cacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                targetCacheStrategies = cacheEvict.targetCacheStrategies.toSet()
            )

            return@runCoroutine value
        }
    }

    private fun precondition(
        joinPoint: ProceedingJoinPoint,
        methodSignature: MethodSignature,
        cacheEvict: CacheEvict,
    ): Boolean {
        val unless = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.args,
            key = cacheEvict.unless,
        )

        val condition = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.args,
            key = cacheEvict.condition,
        )

        return (unless == null || !unless) && (condition == null || condition)
    }

}
