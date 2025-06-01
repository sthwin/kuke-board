package kuke.board.articleread.service

import kuke.board.articleread.client.ArticleClient
import kuke.board.articleread.client.CommentClient
import kuke.board.articleread.client.LikeClient
import kuke.board.articleread.client.ViewClient
import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.repository.BoardArticleCountRepository
import kuke.board.articleread.service.event.handler.EventHandler
import kuke.board.articleread.service.response.ArticleReadPageResponse
import kuke.board.articleread.service.response.ArticleReadResponse
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class ArticleReadService(
    val articleClient: ArticleClient,
    val commentClient: CommentClient,
    val likeClient: LikeClient,
    val viewClient: ViewClient,
    val articleQueryModelRepository: ArticleQueryModelRepository,
    val eventHandlers: List<EventHandler<*>>,
    val articleIdListRepository: ArticleIdListRepository,
    val boardArticleCountRepository: BoardArticleCountRepository,
) {

    val log: Logger = LoggerFactory.getLogger(ArticleReadService::class.java)

    fun handleEvent(event: Event<EventPayload>) {
        eventHandlers
            .filterIsInstance<EventHandler<EventPayload>>()
            .forEach { handler ->
                if (handler.supports(event)) {
                    handler.handle(event)
                }
            }
    }

    fun read(articleId: Long): ArticleReadResponse {
        val articleQueryModel = articleQueryModelRepository.read(articleId)
            ?: fetch(articleId) ?: throw Exception("article not found")

        return ArticleReadResponse.of(
            articleQueryModel,
            viewClient.count(articleId)
        )
    }

    private fun fetch(articleId: Long): ArticleQueryModel? {
        return articleClient.read(articleId)?.let {
            ArticleQueryModel.of(
                it,
                commentClient.count(articleId),
                likeClient.count(articleId)
            )
        }.also {
            log.info("[ArticleReadService.fetch] fetch data. articleId=$articleId, isPresent=${it != null}")
            if (it != null) {
                articleQueryModelRepository.create(it, Duration.ofDays(1))
            }
        }
    }

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long
    ): ArticleReadPageResponse {
        return ArticleReadPageResponse(
            articles = readAll(
                readAllArticleIds(
                    boardId = boardId,
                    page = page,
                    pageSize = pageSize
                )
            ),
            articleCount = count(boardId)
        )
    }

    private fun readAll(articleIds: List<Long>): List<ArticleReadResponse> {
        val articleQueryModelMap = articleQueryModelRepository.readAll(articleIds)

        return articleIds.mapNotNull { articleId ->
            articleQueryModelMap[articleId] ?: fetch(articleId)
        }.map { articleQueryModel ->
            ArticleReadResponse.of(
                articleQueryModel = articleQueryModel,
                viewCount = viewClient.count(articleQueryModel.articleId)
            )
        }
    }

    private fun readAllArticleIds(boardId: Long, page: Long, pageSize: Long): List<Long> {
        val articleIds = articleIdListRepository.readAll(
            boardId = boardId,
            offset = (page - 1) * pageSize,
            limit = pageSize
        )
        return if (articleIds.size.toLong() == pageSize) {
            log.info("[ArticleReadService.readAllArticleIds] return redis data")
            articleIds
        } else {
            log.info("[ArticleReadService.readAllArticleIds] return origin data")
            articleClient.readAll(
                boardId = boardId,
                page = page,
                pageSize = pageSize
            ).articles.map { it.articleId }
        }
    }

    private fun count(boardId: Long): Long {
        return boardArticleCountRepository.read(boardId)
            ?: articleClient.count(boardId).also {
                boardArticleCountRepository.createOrUpdate(boardId, it)
            }
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        pageSize: Long,
        lastArticleId: Long?
    ): List<ArticleReadResponse> {
        return readAll(
            readAllInfiniteScrollArticleIds(
                boardId = boardId,
                pageSize = pageSize,
                lastArticleId = lastArticleId
            )
        )
    }

    private fun readAllInfiniteScrollArticleIds(
        boardId: Long,
        pageSize: Long,
        lastArticleId: Long?
    ): List<Long> {
        val articleIds = articleIdListRepository.readAllInfiniteScroll(
            boardId = boardId,
            lastArticleId = lastArticleId,
            limit = pageSize
        )

        return if (articleIds.size.toLong() == pageSize) {
            log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return redis data")
            articleIds
        } else {
            log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return origin data")
            articleClient.readAllInfiniteScroll(
                boardId = boardId,
                pageSize = pageSize,
                lastArticleId = lastArticleId
            ).map { it.articleId }
        }
    }
}