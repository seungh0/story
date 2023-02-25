package com.story.platform.core.support.lock

import com.story.platform.core.common.utils.SpringExpressionParser
import com.story.platform.core.common.utils.coroutineArgs
import com.story.platform.core.common.utils.proceedCoroutine
import com.story.platform.core.common.utils.runCoroutine
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributeLockAspect(
    private val distributedLockExecutor: DistributedLockExecutor,
) {

    @Around("args(.., kotlin.coroutines.Continuation) && @annotation(DistributeLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val distributeLock = methodSignature.method.getAnnotation(DistributeLock::class.java)

        val lockKey = SpringExpressionParser.parseString(
            methodSignature.parameterNames,
            joinPoint.coroutineArgs,
            distributeLock.key
        )
        return distributedLockExecutor.execute(
            distributeLock = distributeLock,
            lockKey = "lock:${distributeLock.lockType.prefix}:$lockKey}",
        ) {
            return@execute joinPoint.runCoroutine {
                return@runCoroutine joinPoint.proceedCoroutine(joinPoint.coroutineArgs)
            }
        }
    }

}
