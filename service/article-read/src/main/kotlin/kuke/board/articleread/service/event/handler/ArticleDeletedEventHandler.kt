package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.repository.BoardArticleCountRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository,
    val articleIdListRepository: ArticleIdListRepository,
    val boradArticleCountRepository: BoardArticleCountRepository,
) : EventHandler<ArticleDeletedEventPayload> {

    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        // 목록을 먼저 삭제해서 노출되지 않도록 함
        articleIdListRepository.delete(
            boardId = payload.boardId,
            articleId = payload.articleId
        )

        articleQueryModelRepository.delete(payload.articleId)
        boradArticleCountRepository.createOrUpdate(
            boardId = payload.boardId,
            articleCount = payload.boardArticleCount
        )
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean {
        return EventType.ARTICLE_DELETED == event.type
    }
}