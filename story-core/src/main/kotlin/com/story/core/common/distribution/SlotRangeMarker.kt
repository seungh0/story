package com.story.core.common.distribution

import java.util.Base64
import java.util.StringJoiner

data class SlotRangeMarker(
    val startSlotInclusive: Long? = null,
    val startKeyExclusive: String? = null,
    val endSlotExclusive: Long? = null,
) {

    fun makeCursor(): String = encoder.encodeToString(
        StringJoiner(SPLITTER)
            .add(startSlotInclusive.toString())
            .add(if (startKeyExclusive.isNullOrBlank()) null else encoder.encodeToString(startKeyExclusive.toByteArray()))
            .add(endSlotExclusive.toString())
            .toString()
            .toByteArray()
    )

    companion object {
        private val encoder = Base64.getUrlEncoder()
        private val decoder = Base64.getUrlDecoder()

        private const val SPLITTER = "-"

        fun fromCursor(cursor: String): SlotRangeMarker {
            try {
                val (encodedStartSlotInclusive, encodedStartKeyInclusive, encodedEndSlotExclusive) =
                    String(decoder.decode(cursor)).split(SPLITTER)

                val startSlotInclusive = encodedStartSlotInclusive.toLong()
                val startKeyInclusive = if (encodedStartKeyInclusive == "null") {
                    null
                } else {
                    String(decoder.decode(encodedStartKeyInclusive))
                }

                val endSlotExclusive = if (encodedEndSlotExclusive == "null") {
                    null
                } else {
                    encodedEndSlotExclusive.toLong()
                }

                return SlotRangeMarker(
                    startSlotInclusive = startSlotInclusive,
                    startKeyExclusive = startKeyInclusive,
                    endSlotExclusive = endSlotExclusive,
                )
            } catch (exception: Exception) {
                throw IllegalArgumentException("invalid cursor ($cursor)")
            }
        }

        fun fromSlot(
            startSlotNoInclusive: Long,
            endSlotNoExclusive: Long,
        ) = SlotRangeMarker(
            startSlotInclusive = startSlotNoInclusive,
            endSlotExclusive = endSlotNoExclusive,
        )

        fun fromLastSlot(
            startSlotNoInclusive: Long,
        ) = SlotRangeMarker(
            startSlotInclusive = startSlotNoInclusive,
        )
    }

}
