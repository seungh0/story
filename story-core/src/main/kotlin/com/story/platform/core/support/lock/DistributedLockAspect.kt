package com.story.platform.core.support.lock

import com.story.platform.core.support.spring.SpringExpressionParser
import com.story.platform.core.support.spring.coroutineArgs
import com.story.platform.core.support.spring.proceedCoroutine
import com.story.platform.core.support.spring.runCoroutine
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributeLockAspect(
    private val distributedLockProvider: DistributedLockProvider,
) {

    @Around("args(.., kotlin.coroutines.Continuation) && @annotation(DistributeLock)")
    fun handleDistributedLock(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val distributeLock = methodSignature.method.getAnnotation(DistributeLock::class.java)

        val lockKey = SpringExpressionParser.parseString(
            methodSignature.parameterNames,
            joinPoint.coroutineArgs,
            distributeLock.key
        )
        return distributedLockProvider.executeInCriticalSection(
            distributeLock = distributeLock,
            lockKey = "lock:${distributeLock.lockType.prefix}:$lockKey}",
        ) {
            return@executeInCriticalSection joinPoint.runCoroutine {
                return@runCoroutine joinPoint.proceedCoroutine(joinPoint.coroutineArgs)
            }
        }
    }

}
