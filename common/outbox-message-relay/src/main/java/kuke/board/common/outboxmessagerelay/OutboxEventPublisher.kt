package kuke.board.common.outboxmessagerelay

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.snowflake.Snowflake
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OutboxEventPublisher(
    val applicationEventPublisher: ApplicationEventPublisher
) {
    val outboxIdSnowflake: Snowflake = Snowflake()
    val eventIdSnowflake: Snowflake = Snowflake()

    fun publish(
        eventType: EventType,
        payload: EventPayload,
        shardKey: Long
    ) {
        val outbox = Outbox(
            outboxId = outboxIdSnowflake.nextId(),
            eventType = eventType,
            payload = Event.of(
                eventId = eventIdSnowflake.nextId(),
                type = eventType,
                payload = payload
            ).toJson(),
            shardKey = shardKey % MessageRelayConstants.SHARD_COUNT,
            createdAt = LocalDateTime.now()
        )
        applicationEventPublisher.publishEvent(OutboxEvent(outbox = outbox))
    }
}