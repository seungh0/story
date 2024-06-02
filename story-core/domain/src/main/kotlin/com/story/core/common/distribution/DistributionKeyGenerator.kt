package com.story.core.common.distribution

import org.apache.commons.lang3.StringUtils
import java.nio.charset.StandardCharsets
import java.util.zip.CRC32
import kotlin.math.abs

object DistributionKeyGenerator {

    private val NUMERIC_CHARS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    internal fun hashing(format: String, value: String, distributionSize: Int): String {
        val crc = CRC32()
        crc.update(value.toByteArray(StandardCharsets.UTF_8), 0, value.length)
        return String.format(format, abs(crc.value.toInt()) % distributionSize)
    }

    internal fun <T : DistributionKey> makeAllDistributionKeys(
        allKey: MutableList<T>,
        maxDigit: Int,
        func: (String) -> T,
    ) {
        makeAllDistributionKeys(
            allKey,
            listOf(),
            maxDigit,
            0,
            func
        )
    }

    private fun <T : DistributionKey> makeAllDistributionKeys(
        allKey: MutableList<T>,
        chars: List<Char>,
        maxDigit: Int,
        currentDigit: Int,
        func: (String) -> T,
    ) {
        if (currentDigit == maxDigit) {
            allKey.add(func.invoke(StringUtils.join(chars, "")))
            return
        }
        val currentDigit2 = currentDigit + 1
        for (numericChar in NUMERIC_CHARS) {
            val characters: MutableList<Char> = ArrayList(chars)
            characters.add(numericChar)
            makeAllDistributionKeys(
                allKey,
                characters,
                maxDigit,
                currentDigit2,
                func
            )
        }
    }

}
