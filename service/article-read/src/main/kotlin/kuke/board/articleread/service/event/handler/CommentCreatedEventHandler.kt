package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentCreatedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository
) : EventHandler<CommentCreatedEventPayload> {

    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.read(payload.articleId)?.let {
            it.updateBy(event.payload)
            articleQueryModelRepository.update(it)
        }
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean {
        return EventType.COMMENT_CREATED == event.type
    }
}