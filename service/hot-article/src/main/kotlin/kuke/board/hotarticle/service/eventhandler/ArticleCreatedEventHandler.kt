package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleCreatedEventHandler(
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) : EventHandler<ArticleCreatedEventPayload> {

    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleCreatedTimeRepository.createOrUpdate(
            articleId = payload.articleId,
            createdAt = payload.createdAt,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(eventType: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == eventType.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload as ArticleCreatedEventPayload).articleId
    }
}