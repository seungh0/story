package com.story.core.domain.workspace

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class WorkspaceRemoverTest(
    private val workspaceRemover: WorkspaceRemover,
    private val workspaceCassandraRepository: WorkspaceCassandraRepository,
    private val workspaceArchiveRepository: WorkspaceArchiveRepository,
) : StringSpecIntegrationTest({

    "워크스페이스를 삭제하면 워크스페이스 상태를 삭제된 상태로 변경한다" {
        // given
        val workspace = WorkspaceEntityFixture.create(status = WorkspaceStatus.ENABLED)
        workspaceCassandraRepository.save(workspace)

        // when
        workspaceRemover.removeWorkspace(workspaceId = workspace.workspaceId)

        // then
        val workspaces = workspaceCassandraRepository.findAll().toList()
        workspaces shouldHaveSize 1
        workspaces[0].also {
            it.workspaceId shouldBe workspace.workspaceId
            it.plan shouldBe workspace.plan
            it.name shouldBe workspace.name
            it.status shouldBe WorkspaceStatus.DELETED
        }
    }

    "워크스페이스를 삭제하면 아카이빙 워크스페이스에 추가한다" {
        // given
        val workspace = WorkspaceEntityFixture.create(status = WorkspaceStatus.ENABLED)
        workspaceCassandraRepository.save(workspace)

        // when
        workspaceRemover.removeWorkspace(workspaceId = workspace.workspaceId)

        // then
        val workspaceArchives = workspaceArchiveRepository.findAll().toList()
        workspaceArchives shouldHaveSize 1
        workspaceArchives[0].also {
            it.workspaceId shouldBe workspace.workspaceId
            it.plan shouldBe workspace.plan
            it.name shouldBe workspace.name
            it.archiveTime shouldNotBe null
        }
    }

})
