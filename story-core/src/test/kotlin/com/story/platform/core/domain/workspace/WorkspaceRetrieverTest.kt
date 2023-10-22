package com.story.platform.core.domain.workspace

import com.story.platform.core.IntegrationTest
import com.story.platform.core.StringSpecIntegrationTest
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe

@IntegrationTest
class WorkspaceRetrieverTest(
    private val workspaceRetriever: WorkspaceRetriever,
    private val workspaceRepository: WorkspaceRepository,
) : StringSpecIntegrationTest({

    "워크스페이스를 조회한다" {
        // given
        val workspace = WorkspaceFixture.create()
        workspaceRepository.save(workspace)

        // when
        val sut = workspaceRetriever.getWorkspace(workspaceId = workspace.workspaceId)

        // then
        sut.workspaceId shouldBe workspace.workspaceId
        sut.status shouldBe workspace.status
        sut.name shouldBe workspace.name
        sut.plan shouldBe workspace.plan
    }

    "존재하지 않는 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceFixture.create()

        // when & then
        shouldThrowExactly<WorkspaceNotExistsException> {
            workspaceRetriever.getWorkspace(workspaceId = workspace.workspaceId)
        }
    }

})
