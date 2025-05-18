package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleViewedEventPayload
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleViewedEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository
) : EventHandler<ArticleViewedEventPayload> {

    override fun handle(event: Event<ArticleViewedEventPayload>) {
        val payload = event.payload
        articleViewCountRepository.createOrUpdate(
            articleId = payload.articleId,
            viewCount = payload.articleViewCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(eventType: Event<ArticleViewedEventPayload>): Boolean {
        return EventType.ARTICLE_VIEWED == eventType.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload as ArticleViewedEventPayload).articleId
    }
}