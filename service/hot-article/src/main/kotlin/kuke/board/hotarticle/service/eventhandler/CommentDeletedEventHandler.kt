package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentDeletedEventPayload
import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(eventType: Event<out EventPayload>): Boolean {
        return EventType.COMMENT_DELETED == eventType.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload as CommentDeletedEventPayload).articleId
    }
}