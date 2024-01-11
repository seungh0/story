package com.story.api.lib

import com.story.api.DocsTest
import com.story.core.common.error.ErrorCode
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@DocsTest
internal class ErrorCodeDocsGeneratorTest : FunSpec({

    test("에러코드 Asciidoctor 생성") {
        val file = File(FILE_PATH_NAME)
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }
        }
        var asciidoctorText = """
            [cols="5%,30%,30%"]
            |===
            | 상태값 | 에러 코드 | 설명

        """.trimIndent()

        ErrorCode.values()
            .forEach { errorCode ->
                asciidoctorText +=
                    """
                    | ${errorCode.httpStatusCode} | ${errorCode.code} | ${errorCode.description}

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
