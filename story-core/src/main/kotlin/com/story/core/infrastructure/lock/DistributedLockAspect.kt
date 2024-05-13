package com.story.core.infrastructure.lock

import com.story.core.common.coroutine.coroutineArgs
import com.story.core.common.coroutine.proceedCoroutine
import com.story.core.common.coroutine.runCoroutine
import com.story.core.infrastructure.spring.SpringExpressionParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributedLockAspect(
    private val distributedLockHandler: DistributedLockHandler,
) {

    @Around("@annotation(DistributedLock) && args(.., kotlin.coroutines.Continuation)")
    fun handleDistributedLock(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val distributedLock = methodSignature.method.getAnnotation(DistributedLock::class.java)

        return joinPoint.runCoroutine {
            if (!precondition(
                    joinPoint = joinPoint,
                    methodSignature = methodSignature,
                    distributedLock = distributedLock
                )
            ) {
                return@runCoroutine joinPoint.proceedCoroutine()
            }

            val lockKey = SpringExpressionParser.parseString(
                methodSignature.parameterNames,
                joinPoint.coroutineArgs,
                distributedLock.key
            )

            distributedLockHandler.runWithLock(
                distributedLock = distributedLock,
                lockKey = "lock:${distributedLock.lockType.prefix}:$lockKey",
            ) {
                joinPoint.proceedCoroutine()
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
            args = joinPoint.coroutineArgs,
            key = distributedLock.unless,
        )

        val condition = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = distributedLock.condition,
        )

        return (unless == null || !unless) && (condition == null || condition)
    }

}
