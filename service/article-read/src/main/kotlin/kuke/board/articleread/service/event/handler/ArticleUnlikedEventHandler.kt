package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleUnlikedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository
) : EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(payload.articleId)?.let {
            it.updateBy(event.payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean {
        return EventType.ARTICLE_UNLIKED == event.type
    }
}