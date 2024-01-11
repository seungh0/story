package com.story.api

import com.story.core.lib.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
abstract class FunSpecIntegrationTest(body: FunSpec.() -> Unit = {}) : FunSpec() {

    @Autowired
    private lateinit var testCleaner: TestCleaner

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        testCleaner.cleanUp()
    }

    init {
        body()
    }

}
