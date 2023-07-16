package com.story.platform.core.common.sequence

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import org.springframework.util.StopWatch

class SnowflakeIdGeneratorTest : FunSpec({

    test("SnowflakeId를 채번한다") {
        // given
        val idGenerator = SnowflakeIdGenerator()

        // when
        val one = idGenerator.nextId()
        val two = idGenerator.nextId()
        val three = idGenerator.nextId()

        // then
        one shouldBeLessThan two
        two shouldBeLessThan three
    }

    test("Benchmark [0.3ms 이내에 채번되어야 한다]") {
        // given
        val idGenerator = SnowflakeIdGenerator()

        val stopWatch = StopWatch()
        stopWatch.start()

        // when
        idGenerator.nextId()

        stopWatch.stop()
        val totalTimeMillis = stopWatch.totalTimeNanos

        // then
        totalTimeMillis shouldBeLessThanOrEqual 300_000 // 0.3ms
    }

})
