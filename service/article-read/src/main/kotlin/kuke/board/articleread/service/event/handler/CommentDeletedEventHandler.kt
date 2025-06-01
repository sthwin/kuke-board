package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository
) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(payload.articleId)?.let {
            it.updateBy(event.payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.COMMENT_DELETED == event.type
    }
}