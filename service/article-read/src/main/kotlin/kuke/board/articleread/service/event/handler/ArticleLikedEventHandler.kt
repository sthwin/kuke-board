package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleLikedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository
) : EventHandler<ArticleLikedEventPayload> {

    override fun handle(event: Event<ArticleLikedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(payload.articleId)?.let {
            it.updateBy(event.payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean {
        return EventType.ARTICLE_LIKED == event.type
    }
}