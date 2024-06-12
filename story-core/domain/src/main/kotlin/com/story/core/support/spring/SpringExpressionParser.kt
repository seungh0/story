package com.story.core.support.spring

import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

object SpringExpressionParser {

    fun parseString(parameterNames: Array<String>, args: Array<Any?>, key: String): String? {
        if (key.isBlank()) {
            return ""
        }
        val expressionParser: ExpressionParser = SpelExpressionParser()
        val context = StandardEvaluationContext()
        context.setVariables(parameterNames.zip(args).toMap())
        return expressionParser.parseExpression(key).getValue(context, String::class.java)
    }

    fun parseBoolean(parameterNames: Array<String>, args: Array<Any?>, key: String): Boolean? {
        if (key.isBlank()) {
            return null
        }
        val expressionParser: ExpressionParser = SpelExpressionParser()
        val context = StandardEvaluationContext()
        context.setVariables(parameterNames.zip(args).toMap())
        return expressionParser.parseExpression(key).getValue(context, Boolean::class.java)
    }

}
