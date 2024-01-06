package com.story.platform.api.application.workspace

import com.story.platform.core.domain.workspace.WorkspaceFixture
import com.story.platform.core.domain.workspace.WorkspaceNotExistsException
import com.story.platform.core.domain.workspace.WorkspaceResponse
import com.story.platform.core.domain.workspace.WorkspaceRetriever
import com.story.platform.core.domain.workspace.WorkspaceStatus
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk

class WorkspaceRetrieveHandlerTest : StringSpec({

    val workspaceRetriever = mockk<WorkspaceRetriever>()
    val workspaceRetrieverHandler = WorkspaceRetrieveHandler(
        workspaceRetriever = workspaceRetriever,
    )

    "존재하지 않는 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceFixture.create(status = WorkspaceStatus.ENABLED)
        coEvery { workspaceRetriever.getWorkspace(any()) } returns WorkspaceResponse.of(workspace)

        // when & then
        shouldNotThrowAny {
            workspaceRetrieverHandler.validateEnabledWorkspace(workspaceId = workspace.workspaceId)
        }
    }

    "삭제된 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceFixture.create(status = WorkspaceStatus.DELETED)
        coEvery { workspaceRetriever.getWorkspace(any()) } returns WorkspaceResponse.of(workspace)

        // when & then
        shouldThrowExactly<WorkspaceNotExistsException> {
            workspaceRetrieverHandler.validateEnabledWorkspace(workspaceId = workspace.workspaceId)
        }
    }

})
