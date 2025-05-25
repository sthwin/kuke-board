package kuke.board.common.outboxmessagerelay

import jakarta.persistence.*
import kuke.board.common.event.EventType
import java.time.LocalDateTime


@Table(name = "outbox")
@Entity
data class Outbox(
    @Id
    val outboxId: Long,
    @Enumerated(EnumType.STRING)
    val eventType: EventType,
    val payload: String,
    val shardKey: Long,
    val createdAt: LocalDateTime,
)