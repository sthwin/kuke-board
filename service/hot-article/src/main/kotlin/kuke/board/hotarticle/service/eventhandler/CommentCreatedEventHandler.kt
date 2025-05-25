package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.CommentCreatedEventPayload
import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository
) : EventHandler<CommentCreatedEventPayload> {

    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount,
            ttl = TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(eventType: Event<out EventPayload>): Boolean {
        return EventType.COMMENT_CREATED == eventType.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload as CommentCreatedEventPayload).articleId
    }
}