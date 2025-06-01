package kuke.board.articleread.consumer

import kuke.board.articleread.service.ArticleReadService
import kuke.board.common.event.Event
import kuke.board.common.event.Topic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ArticleReadEventConsumer(
    val articleReadService: ArticleReadService,
) {

    val log: Logger = LoggerFactory.getLogger(ArticleReadEventConsumer::class.java)

    @KafkaListener(
        topics = [
            Topic.KUKE_BOARD_ARTICLE,
            Topic.KUKE_BOARD_COMMENT,
            Topic.KUKE_BOARD_LIKE,
        ]
    )
    fun listen(
        message: String,
        ack: Acknowledgment
    ) {
        log.info("[ArticleReadEventConsumer.listen] message=$message")
        Event.fromJson(message).let {
            articleReadService.handleEvent(it)
            ack.acknowledge()
        }
    }
}