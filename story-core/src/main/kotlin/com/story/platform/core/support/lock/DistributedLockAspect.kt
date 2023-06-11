package com.story.platform.core.support.lock

import com.story.platform.core.support.coroutine.coroutineArgs
import com.story.platform.core.support.coroutine.proceedCoroutine
import com.story.platform.core.support.coroutine.runCoroutine
import com.story.platform.core.support.spring.SpringExpressionParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributedLockAspect(
    private val redissonDistributedLock: RedissonDistributedLock,
) {

    @Around("args(.., kotlin.coroutines.Continuation) && @annotation(DistributedLock)")
    fun handleDistributedLock(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val distributedLock = methodSignature.method.getAnnotation(DistributedLock::class.java)

        if (!precondition(
                joinPoint = joinPoint,
                methodSignature = methodSignature,
                distributedLock = distributedLock
            )
        ) {
            return joinPoint.proceed(joinPoint.args)
        }

        return joinPoint.runCoroutine {
            val lockKey = SpringExpressionParser.parseString(
                methodSignature.parameterNames,
                joinPoint.coroutineArgs,
                distributedLock.key
            )
            return@runCoroutine redissonDistributedLock.executeInCriticalSection(
                distributedLock = distributedLock,
                lockKey = "lock:${distributedLock.lockType.prefix}:$lockKey}",
            ) {
                return@executeInCriticalSection joinPoint.runCoroutine {
                    return@runCoroutine joinPoint.proceedCoroutine(joinPoint.coroutineArgs)
                }
            }
        }
    }

    private fun precondition(
        joinPoint: ProceedingJoinPoint,
        methodSignature: MethodSignature,
        distributedLock: DistributedLock,
    ): Boolean {
        val unless = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.args,
            key = distributedLock.unless,
        )

        val condition = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.args,
            key = distributedLock.condition,
        )

        return (unless == null || !unless) && (condition == null || condition)
    }

}
