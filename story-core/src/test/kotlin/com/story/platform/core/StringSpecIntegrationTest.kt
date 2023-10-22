package com.story.platform.core

import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
abstract class StringSpecIntegrationTest(body: StringSpec.() -> Unit = {}) : StringSpec() {

    @Autowired
    private lateinit var testCleaner: TestCleaner

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        testCleaner.cleanUp()
    }

    init {
        body()
    }

}
