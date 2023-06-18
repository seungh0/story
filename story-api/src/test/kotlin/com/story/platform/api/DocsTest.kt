package com.story.platform.api

import io.kotest.core.annotation.Tags
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient

@Tags("docs-test")
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
internal annotation class DocsTest
