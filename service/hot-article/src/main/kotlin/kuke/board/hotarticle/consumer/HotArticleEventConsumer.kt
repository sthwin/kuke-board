package kuke.board.hotarticle.consumer

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.Topic
import kuke.board.hotarticle.service.HotArticleService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class HotArticleEventConsumer(
    val hotArticleService: HotArticleService
) {

    val logger: Logger = LoggerFactory.getLogger(HotArticleEventConsumer::class.java)

    @KafkaListener(
        topics = [
            Topic.KUKE_BOARD_ARTICLE,
            Topic.KUKE_BOARD_COMMENT,
            Topic.KUKE_BOARD_LIKE,
            Topic.KUKE_BOARD_VIEW,
        ]
    )
    fun listen(message: String, ack: Acknowledgment) {
        logger.info("[HotArticleEventConsumer.listen] received message={}", message)
        Event.fromJson<EventPayload>(message).let {
            hotArticleService.handleEvent(it)
            ack.acknowledge()
        }
    }
}