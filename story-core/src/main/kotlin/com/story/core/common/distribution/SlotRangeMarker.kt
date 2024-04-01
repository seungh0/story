package com.story.core.common.distribution

import java.util.Base64
import java.util.StringJoiner

data class SlotRangeMarker(
    val startSlotInclusive: Long? = null,
    val startKeyInclusive: String = "",
    val endSlotExclusive: Long? = null,
    val endKeyInclusive: String = "",
) {

    fun makeCursor(): String = encoder.encodeToString(
        StringJoiner(SPLITTER)
            .add(startSlotInclusive.toString())
            .add(encoder.encodeToString(startKeyInclusive.toByteArray()))
            .add(endSlotExclusive.toString())
            .add(endKeyInclusive)
            .toString()
            .toByteArray()
    )

    companion object {
        private val encoder = Base64.getUrlEncoder()
        private val decoder = Base64.getUrlDecoder()

        private const val SPLITTER = "/"

        fun fromCursor(cursor: String): SlotRangeMarker {
            try {
                val (encodedstartSlotInclusive, encodedStartKeyInclusive, encodedEndSlotEnclusive, encodedEndKeyInclusive) = String(
                    decoder.decode(cursor)
                ).split(SPLITTER)

                val startSlotInclusive = encodedstartSlotInclusive.toLong()
                val startKeyInclusive = String(decoder.decode(encodedStartKeyInclusive))
                val endSlotEnclusive = encodedEndSlotEnclusive.toLong()
                val endKeyInclusive = String(decoder.decode(encodedEndKeyInclusive))

                return SlotRangeMarker(
                    startSlotInclusive = startSlotInclusive,
                    startKeyInclusive = startKeyInclusive,
                    endSlotExclusive = endSlotEnclusive,
                    endKeyInclusive = endKeyInclusive,
                )
            } catch (exception: Exception) {
                throw IllegalArgumentException("invalid cursor")
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
