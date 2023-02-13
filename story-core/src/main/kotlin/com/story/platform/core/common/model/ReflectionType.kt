package com.story.platform.core.common.model

import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class ReflectionType(
    val returnType: Class<*>,
    val actualType: Type,
) {

    companion object {
        fun getType(methodSignature: MethodSignature): ReflectionType {
            val methodSignatureReturnType = methodSignature.returnType
            val genericReturnType = methodSignature.method.genericReturnType

            if (genericReturnType is ParameterizedType) {
                val actualType = genericReturnType.actualTypeArguments[0]
                if (actualType is Class<*>) {
                    return ReflectionType(returnType = methodSignatureReturnType, actualType = actualType)
                }
            }
            return ReflectionType(returnType = methodSignatureReturnType, actualType = methodSignatureReturnType)
        }
    }

}
