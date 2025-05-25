package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    val hotArticleListRepository: HotArticleListRepository,
    val articleCreatedTimeRepository: ArticleCreatedTimeRepository
) : EventHandler<ArticleDeletedEventPayload> {

    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        event.payload.also { payload ->
            articleCreatedTimeRepository.delete(payload.articleId)
            hotArticleListRepository.remove(
                articleId = payload.articleId,
                time = payload.createdAt
            )
        }
    }

    override fun supports(eventType: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_DELETED == eventType.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload as ArticleDeletedEventPayload).articleId
    }
}