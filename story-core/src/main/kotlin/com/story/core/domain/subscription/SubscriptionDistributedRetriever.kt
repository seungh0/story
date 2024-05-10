package com.story.core.domain.subscription

import com.story.core.common.distribution.RangePartitioner
import com.story.core.common.distribution.SlotRangeMarker
import com.story.core.common.model.dto.SlotRangeMarkerResponse
import com.story.core.domain.subscription.SubscriptionSlotAssigner.FIRST_SLOT_ID
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class SubscriptionDistributedRetriever(
    private val subscriberRepository: SubscriberRepository,
    private val subscriberSequenceRepository: SubscriberSequenceRepository,
) {

    suspend fun getSubscriberDistributedMarkers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        markerSize: Int,
    ): List<SlotRangeMarker> {
        val lastSequence = subscriberSequenceRepository.getLastSequence(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId
        )
        if (lastSequence <= 0) {
            return emptyList()
        }

        val slotMarkers: List<Long> = RangePartitioner.partition(
            startInclusive = FIRST_SLOT_ID,
            endInclusive = SubscriptionSlotAssigner.assign(lastSequence),
            partitionSize = markerSize,
        )

        return slotMarkers.mapIndexed { index, marker ->
            if (index > 0) {
                return@mapIndexed SlotRangeMarker.fromSlot(
                    startSlotNoInclusive = marker,
                    endSlotNoExclusive = slotMarkers[index - 1],
                )
            } else {
                return@mapIndexed SlotRangeMarker.fromLastSlot(
                    startSlotNoInclusive = marker,
                )
            }
        }
    }

    suspend fun listSubscribersByDistributedmarkers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        marker: SlotRangeMarker,
        pageSize: Int,
    ): SlotRangeMarkerResponse<List<Subscription>> {
        val startSlot = marker.startSlotInclusive
            ?: throw IllegalArgumentException("Invalid marker for distributed query. The startSlotInclusive parameter must be not null")

        val subscribers = mutableListOf<SubscriberEntity>()

        var currentSlot = startSlot
        val endSlot = marker.endSlotExclusive ?: (FIRST_SLOT_ID - 1)

        var currentCursor = marker.startKeyExclusive

        var remainingRecordsSize = pageSize

        var hasMoreRecords = true

        while (currentSlot > endSlot && remainingRecordsSize > 0) {
            val currentSubscribersWithNextCursor = if (currentCursor.isNullOrBlank()) {
                subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    slotId = currentSlot,
                    pageable = CassandraPageRequest.first(remainingRecordsSize + 1),
                ).toList()
            } else {
                subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThanOrderByKeySubscriberIdDesc(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    slotId = currentSlot,
                    subscriberId = currentCursor,
                    pageable = CassandraPageRequest.first(remainingRecordsSize + 1),
                ).toList()
            }

            val currentSubscribers = currentSubscribersWithNextCursor.subList(
                0,
                remainingRecordsSize.coerceAtMost(currentSubscribersWithNextCursor.size)
            )

            subscribers += currentSubscribers

            if (currentSubscribersWithNextCursor.size > remainingRecordsSize) {
                currentCursor = currentSubscribers.last().key.subscriberId
                hasMoreRecords = true
            } else {
                currentCursor = null
                currentSlot -= 1
                hasMoreRecords = currentSlot > endSlot
            }

            remainingRecordsSize -= currentSubscribers.size
        }

        return SlotRangeMarkerResponse(
            data = subscribers.map { subscriber -> Subscription.of(subscriber) },
            nextMarker = if (hasMoreRecords) {
                marker.copy(
                    startSlotInclusive = currentSlot,
                    startKeyExclusive = currentCursor,
                )
            } else null
        )
    }

}
