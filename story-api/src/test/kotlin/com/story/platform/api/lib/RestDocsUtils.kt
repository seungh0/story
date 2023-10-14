package com.story.platform.api.lib

import com.story.platform.core.common.http.HttpHeader
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.snippet.Attributes
import java.util.EnumSet
import java.util.UUID

object RestDocsUtils {

    fun getDocumentRequest(): OperationRequestPreprocessor {
        return preprocessRequest(
            modifyUris()
                .scheme("http")
                .host("localhost")
                .port(8000),
            prettyPrint(),
            modifyHeaders()
                .remove("Content-Length")
                .remove("Host")
                .remove("Host")
        )
    }

    private fun attribute(key: String, value: Any?): Attributes.Attribute {
        return Attributes.Attribute(key, value)
    }

    fun remarks(value: Any?): Attributes.Attribute {
        return attribute("remarks", value)
    }

    fun <T : Enum<T>?> convertToString(enumClass: Class<T>): String {
        return EnumSet.allOf(enumClass).joinToString(separator = ", ") { it!!.name }
    }

    fun getDocumentResponse(): OperationResponsePreprocessor {
        return preprocessResponse(
            prettyPrint(),
            modifyHeaders()
                .remove("Cache-Control")
                .remove("Pragma")
                .remove("Expires")
                .remove("X-Content-Type-Options")
                .remove("X-Frame-Options")
                .remove("X-XSS-Protection")
                .remove("Referrer-Policy")
                .remove("Content-Length")
        )
    }

    val commonHeadersDocumentation = HeaderDocumentation.requestHeaders(
        headerWithName(HttpHeader.X_FORWARDED_FOR.header).description("X-Forwarded-For")
            .attributes(remarks("127.0.0.1"))
            .optional(),
        headerWithName(HttpHeader.X_REQUEST_ID.header).description("X-Request-Id")
            .attributes(remarks(UUID.randomUUID().toString()))
            .optional(),
    )

    val authenticationHeaderDocumentation = HeaderDocumentation.requestHeaders(
        headerWithName(HttpHeader.X_FORWARDED_FOR.header).description("X-Forwarded-For")
            .attributes(remarks("127.0.0.1"))
            .optional(),
        headerWithName(HttpHeader.X_REQUEST_ID.header).description("X-Request-Id")
            .attributes(remarks(UUID.randomUUID().toString()))
            .optional(),
        headerWithName(HttpHeader.X_STORY_API_KEY.header).description("인증 키"),
    )

    val authenticationHeaderWithRequestAccountIdDocumentation = HeaderDocumentation.requestHeaders(
        headerWithName(HttpHeader.X_FORWARDED_FOR.header).description("X-Forwarded-For")
            .attributes(remarks("127.0.0.1"))
            .optional(),
        headerWithName(HttpHeader.X_REQUEST_ID.header).description("X-Request-Id")
            .attributes(remarks(UUID.randomUUID().toString()))
            .optional(),
        headerWithName(HttpHeader.X_STORY_API_KEY.header).description("인증 키"),
        headerWithName(HttpHeader.X_STORY_REQUEST_ACCOUNT_ID.header).description("요청자의 계정 ID"),
    )

}
