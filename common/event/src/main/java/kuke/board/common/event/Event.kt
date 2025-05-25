package kuke.board.common.event

import kuke.board.common.dataserializer.DataSerializer

data class Event<T : EventPayload>(
    val eventId: Long,
    val type: EventType,
    val payload: T,
) {
    companion object {
        fun <T : EventPayload> of(eventId: Long, type: EventType, payload: T): Event<T> {
            return Event(
                eventId = eventId,
                type = type,
                payload = payload
            )
        }

        fun fromJson(json: String): Event<EventPayload> {
            val eventRaw = DataSerializer.deserialize<EventRaw>(json)
            return Event(
                eventId = eventRaw.eventId,
                type = eventRaw.type,
                payload = DataSerializer.deserialize(
                    data = eventRaw.payload,
                    clazz = eventRaw.type.payloadClass.java
                )
            )
        }
    }

    fun toJson(): String {
        return DataSerializer.serialize(this)
    }

    data class EventRaw(
        val eventId: Long,
        val type: EventType,
        val payload: Any,
    )
}