package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleUnlikedEventPayload
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    val articleLikeCountRepository: ArticleLikeCountRepository
) : EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        event.payload.also { payload ->
            articleLikeCountRepository.createOrUpdate(
                articleId = payload.articleId,
                likeCount = payload.articleLikeCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override fun supports(eventType: Event<out EventPayload>): Boolean {
        return EventType.ARTICLE_UNLIKED == eventType.type
    }

    override fun findArticleId(event: Event<out EventPayload>): Long {
        return (event.payload as ArticleUnlikedEventPayload).articleId
    }
}