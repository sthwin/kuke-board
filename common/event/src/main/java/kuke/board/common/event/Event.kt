package kuke.board.common.event

import kuke.board.common.dataserializer.DataSerializer

data class Event<T : EventPayload>(
    val eventId: Long,
    val type: EventType,
    val payload: T,
) {
    companion object {
        fun of(eventId: Long, type: EventType, payload: EventPayload): Event<EventPayload> {
            return Event(
                eventId = eventId,
                type = type,
                payload = payload
            )
        }

        inline fun <reified T : EventPayload> fromJson(json: String): Event<T> {
            return DataSerializer.deserialize<Event<T>>(json)
        }
    }

    fun toJson(): String {
        return DataSerializer.serialize(this)
    }
}