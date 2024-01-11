package com.story.core.domain.workspace

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class WorkspaceArchiveTest : StringSpec({

    "아카이빙 후 90일이 지나지 않은 워크스페이스는 삭제할 수 없다" {
        // given
        val archiveTime = LocalDateTime.of(2023, 1, 1, 0, 0)
        val archive = WorkspaceArchive.from(WorkspaceFixture.create(), archiveTime = archiveTime)

        // when
        val sut = archive.canPurge(now = archiveTime.plusDays(89))

        // then
        sut shouldBe false
    }

    "아카이빙 후 90일이 지난 워크스페이스는 삭제할 수 있다" {
        // given
        val archiveTime = LocalDateTime.of(2023, 1, 1, 0, 0)
        val archive = WorkspaceArchive.from(WorkspaceFixture.create(), archiveTime = archiveTime)

        // when
        val sut = archive.canPurge(now = archiveTime.plusDays(90))

        // then
        sut shouldBe true
    }

})
