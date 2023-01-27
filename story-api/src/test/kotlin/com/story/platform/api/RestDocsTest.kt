package com.story.platform.api

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient

@Tag("docs-test")
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
internal abstract class RestDocsTest : ApiTestBase()
