package com.story.platform.api

import io.kotest.core.annotation.Tags
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("docs-test")
@AutoConfigureRestDocs
internal annotation class DocsTest
