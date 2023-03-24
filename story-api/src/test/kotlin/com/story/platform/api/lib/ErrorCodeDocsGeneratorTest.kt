package com.story.platform.api.lib

import com.story.platform.api.RestDocsTest
import com.story.platform.core.common.error.ErrorCode
import io.kotest.core.spec.style.FunSpec
import java.io.File

@RestDocsTest
internal class ErrorCodeDocsGeneratorTest : FunSpec({

    test("에러코드 Asciidoctor 생성") {
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

}) {

    companion object {
        private const val FILE_PATH_NAME = "src/docs/asciidoc/restapi/error.adoc"
    }

}
