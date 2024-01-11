package com.story.core.common.distribution

object RangePartitioner {

    fun partition(startInclusive: Long, endInclusive: Long, numOfPartitions: Int): List<Long> {
        require(
            startInclusive <= endInclusive
        ) { "startInclusive($startInclusive)은 endInclusive($endInclusive)보다 클 수 없습니다" }

        require(numOfPartitions > 0) {
            "파티션 수($numOfPartitions)는 0보다 커야합니다"
        }

        val range = endInclusive - startInclusive + 1
        val quotient = range / numOfPartitions
        val remainder = range % numOfPartitions

        if (quotient <= 0) {
            return partition(
                startInclusive = startInclusive,
                endInclusive = endInclusive,
                numOfPartitions = remainder.toInt(),
            )
        }

        val result = mutableListOf<Long>()
        var mark = endInclusive

        repeat(numOfPartitions) { i ->
            result.add(mark)
            mark = if (i < remainder) {
                mark - quotient - 1
            } else {
                mark - quotient
            }
        }

        return result.reversed()
    }

}
