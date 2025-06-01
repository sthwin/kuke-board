package kuke.board.articleread.service.event.handler

import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.repository.BoardArticleCountRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    val articleQueryModelRepository: ArticleQueryModelRepository,
    val articleIdListRepository: ArticleIdListRepository,
    val boardArticleCountRepository: BoardArticleCountRepository,
) : EventHandler<ArticleCreatedEventPayload> {

    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.create(
            ArticleQueryModel.of(payload),
            Duration.ofDays(1)
        )
        articleIdListRepository.add(
            boardId = payload.boardId,
            articleId = payload.articleId,
            limit = 1000L
        )
        boardArticleCountRepository.createOrUpdate(
            boardId = payload.boardId,
            articleCount = payload.boardArticleCount
        )
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == event.type
    }
}