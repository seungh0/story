package com.story.core.infrastructure.cache

import com.story.core.common.coroutine.coroutineArgs
import com.story.core.common.coroutine.proceedCoroutine
import com.story.core.common.coroutine.runCoroutine
import com.story.core.common.error.InternalServerException
import com.story.core.infrastructure.spring.SpringExpressionParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class CacheableAspect(
    private val cacheManager: CacheManager,
) {

    @Around("args(.., kotlin.coroutines.Continuation) && @annotation(Cacheable)")
    fun handleLayeredCache(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val cacheable = methodSignature.method.getAnnotation(Cacheable::class.java)

        return joinPoint.runCoroutine {
            if (!precondition(joinPoint = joinPoint, methodSignature = methodSignature, cacheable = cacheable)) {
                return@runCoroutine joinPoint.proceedCoroutine(joinPoint.coroutineArgs)
            }

            val cacheKey = SpringExpressionParser.parseString(
                parameterNames = methodSignature.parameterNames,
                args = joinPoint.coroutineArgs,
                key = cacheable.key,
            )

            if (cacheKey.isNullOrBlank()) {
                throw InternalServerException("@Cacheable key can't be blank. [cacheType: ${cacheable.cacheType} parameterNames: ${methodSignature.parameterNames} args: ${joinPoint.args} key: ${cacheable.key}]")
            }

            val cacheValue = cacheManager.getCacheFromLayeredCache(
                cacheType = cacheable.cacheType,
                cacheKey = cacheKey,
            )
            if (cacheValue != null) {
                return@runCoroutine cacheValue
            }

            val value = joinPoint.proceedCoroutine(joinPoint.coroutineArgs)

            if (value != null) {
                cacheManager.refreshCacheLayeredCache(
                    cacheType = cacheable.cacheType,
                    cacheKey = cacheKey,
                    value = value
                )
            }

            return@runCoroutine value
        }
    }

    private suspend fun precondition(
        joinPoint: ProceedingJoinPoint,
        methodSignature: MethodSignature,
        cacheable: Cacheable,
    ): Boolean {
        val unless = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = cacheable.unless,
        )

        val condition = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = cacheable.condition,
        )

        return (unless == null || !unless) && (condition == null || condition)
    }

}
