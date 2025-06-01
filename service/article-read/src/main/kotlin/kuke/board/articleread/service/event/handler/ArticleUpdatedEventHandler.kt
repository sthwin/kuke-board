package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleUpdatedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleUpdatedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository
) : EventHandler<ArticleUpdatedEventPayload> {

    override fun handle(event: Event<ArticleUpdatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(payload.articleId)?.let {
            it.updateBy(event.payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<ArticleUpdatedEventPayload>): Boolean {
        return EventType.ARTICLE_UPDATED == event.type
    }
}