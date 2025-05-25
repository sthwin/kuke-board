package kuke.board.common.outboxmessagerelay

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class MessageRelay(
    val outboxRepository: OutboxRepository,
    val messageRelayCoordinator: MessageRelayCoordinator,
    val messageRelayKafkaTemplate: KafkaTemplate<String, String>,
) {

    val logger: Logger = LoggerFactory.getLogger(MessageRelay::class.java)

    /**
     * 커밋되기 전에 OutboxEvent를 받아서 저장한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun createOutbox(outboxEvent: OutboxEvent) {
        logger.info("[Message.createOutbox] outboxEvent=$outboxEvent")
        outboxRepository.save(outboxEvent.outbox)
    }

    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishEvent(outboxEvent: OutboxEvent) {
        publishEvent(outboxEvent.outbox)
    }

    private fun publishEvent(outbox: Outbox) {
        messageRelayKafkaTemplate.send(
            outbox.eventType.topic,
            outbox.shardKey.toString(),
            outbox.payload
        ).get(1, TimeUnit.SECONDS)
        outboxRepository.delete(outbox)
    }

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS,
        scheduler = "messageReplayPublishPendingEventExecutor"
    )
    fun publishPendingEvent() {
        val assignedShard = messageRelayCoordinator.assignShards()
        logger.info("[Message.publishPendingEvent] assignedShard size=${assignedShard.shards.size}")
        assignedShard.shards.forEach { shardKey ->
            val outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                shardKey,
                LocalDateTime.now().minusSeconds(10),
                Pageable.ofSize(100)
            )
            outboxes.forEach {
                publishEvent(it)
            }
        }
    }
}