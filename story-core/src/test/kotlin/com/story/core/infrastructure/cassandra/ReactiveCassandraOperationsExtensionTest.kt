package com.story.core.infrastructure.cassandra

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import com.story.core.domain.workspace.WorkspaceFixture
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate

@IntegrationTest
class ReactiveCassandraOperationsExtensionTest(
    private val reactiveCassandraTemplate: ReactiveCassandraTemplate,
) : StringSpecIntegrationTest({

    "withIfNotExists - 해당 데이터가 이미 있는 경우 wasApplied return false" {
        // given
        val workspace = WorkspaceFixture.create()
        reactiveCassandraTemplate.insert(workspace).awaitSingle()

        // when
        val sut = reactiveCassandraTemplate.insertIfNotExists(workspace).awaitSingle()

        // then
        sut.wasApplied() shouldBe false
    }

    "withIfNotExists - 해당 데이터가 없는 경우 wasApplied return true" {
        // given
        val workspace = WorkspaceFixture.create()

        // when
        val sut = reactiveCassandraTemplate.insertIfNotExists(workspace).awaitSingle()

        // then
        sut.wasApplied() shouldBe true
    }

    "delete withIfExists - 해당 데이터가 이미 있는 경우 wasApplied true" {
        // given
        val workspace = WorkspaceFixture.create()
        reactiveCassandraTemplate.insert(workspace).awaitSingle()

        // when
        val sut = reactiveCassandraTemplate.deleteIfExists(workspace).awaitSingle()

        // then
        sut.wasApplied() shouldBe true
    }

    "delete withIfExists - 해당 데이터가 없는 경우 wasApplied false" {
        // given
        val workspace = WorkspaceFixture.create()

        // when
        val sut = reactiveCassandraTemplate.deleteIfExists(workspace).awaitSingle()

        // then
        sut.wasApplied() shouldBe false
    }

})
