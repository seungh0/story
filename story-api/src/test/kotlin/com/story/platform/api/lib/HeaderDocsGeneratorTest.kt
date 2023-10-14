package com.story.platform.api.lib

import com.story.platform.api.DocsTest
import com.story.platform.core.common.http.HttpHeader
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@DocsTest
internal class HeaderDocsGeneratorTest : FunSpec({

    test("Header Asciidoctor 생성") {
        val file = File(FILE_PATH_NAME)
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }
        }
        var asciidoctorText = """
            [cols="5%,30%,30%"]
            |===
            | Http Header | Description | Example

        """.trimIndent()

        HttpHeader.values()
            .forEach { httpHeader ->
                asciidoctorText +=
                    """
                    | ${httpHeader.header} | ${httpHeader.description} | ${httpHeader.example}

                    """.trimIndent()
            }

        asciidoctorText += "|===\n".trim()

        file.printWriter().use { out -> out.println(asciidoctorText) }
    }

}) {

    companion object {
        private const val FILE_PATH_NAME = "src/docs/asciidoc/restapi/header.adoc"
    }

}
