package com.story.platform.core.domain.workspace

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@IntegrationTest
class WorkspaceRetrieverTest(
    private val workspaceRetriever: WorkspaceRetriever,
    private val workspaceRepository: WorkspaceRepository,
    private val testCleaner: TestCleaner,
) : StringSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    "워크스페이스를 조회한다" {
        // given
        val workspace = WorkspaceFixture.create(status = WorkspaceStatus.ENABLED)
        workspaceRepository.save(workspace)

        // when
        val sut = workspaceRetriever.getWorkspace(workspaceId = workspace.workspaceId)

        // then
        sut.workspaceId shouldBe workspace.workspaceId
        sut.status shouldBe workspace.status
        sut.name shouldBe workspace.name
        sut.pricePlan shouldBe workspace.pricePlan
    }

    "워크스페이스 조회시 없는 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceFixture.create()

        // when & then
        shouldThrowExactly<WorkspaceNotExistsException> {
            workspaceRetriever.getWorkspace(workspaceId = workspace.workspaceId)
        }
    }

    "워크스페이스 삭제된 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceFixture.create(status = WorkspaceStatus.DELETED)
        workspaceRepository.save(workspace)

        // when & then
        shouldThrowExactly<WorkspaceNotExistsException> {
            workspaceRetriever.getWorkspace(workspaceId = workspace.workspaceId)
        }
    }

})
