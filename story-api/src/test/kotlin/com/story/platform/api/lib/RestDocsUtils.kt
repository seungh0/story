package com.story.platform.api.lib

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.snippet.Attributes
import java.util.*

object RestDocsUtils {

    fun identifier(identifier: String = ""): String = identifier

    fun getDocumentRequest(): OperationRequestPreprocessor {
        return preprocessRequest(
            modifyUris()
                .scheme("http")
                .host("localhost")
                .port(8000),
            prettyPrint()
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
        return preprocessResponse(prettyPrint())
    }

}
