package com.story.datacenter.api.lib

import com.story.datacenter.api.RestDocsTest
import com.story.datacenter.core.common.error.ErrorCode
import org.junit.jupiter.api.Test
import java.io.File

internal class ErrorCodeDocsGeneratorTest : RestDocsTest() {

    @Test
    fun `ErrorCode Docs`() {
        val file = File(FILE_PATH_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        var asciidoctorText = """
            [cols="3%,10%,70%"]
            |===
            | Http status | Minor Status Code | Error Description

            """.trimIndent()

        ErrorCode.values()
            .forEach { errorCode ->
                asciidoctorText +=
                    """
                    | ${errorCode.httpStatusCode} | ${errorCode.minorStatusCode} | ${errorCode.errorMessage}

                    """.trimIndent()
            }

        asciidoctorText += "|===\n".trim()

        file.printWriter().use { out -> out.println(asciidoctorText) }
    }

    companion object {
        private const val FILE_PATH_NAME = "src/docs/asciidoc/restapi/error.adoc"
    }

}
