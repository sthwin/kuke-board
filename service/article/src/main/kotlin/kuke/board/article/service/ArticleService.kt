package kuke.board.article.service

import kuke.board.article.entity.Article
import kuke.board.article.entity.BoardArticleCount
import kuke.board.article.repository.ArticleRepository
import kuke.board.article.repository.BoardArticleCountRepository
import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticlePageResponse
import kuke.board.article.service.response.ArticleResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class ArticleService(
    val articleRepository: ArticleRepository,
    val boardArticleCountRepository: BoardArticleCountRepository,
) {
    val snowflake = Snowflake()

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = Article(
            articleId = snowflake.nextId(),
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId
        )

        val result = boardArticleCountRepository.increase(request.boardId)
        if (result == 0) {
            boardArticleCountRepository.save(
                BoardArticleCount.init(
                    boardId = request.boardId,
                    articleCount = 1L
                )
            )
        }

        return ArticleResponse.of(articleRepository.save(article))
    }

    @Transactional
    fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articleRepository.findById(articleId).orElseThrow()
        article.update(request.title, request.content)
        return ArticleResponse.of(article)
    }

    fun read(articleId: Long): ArticleResponse {
        val article = articleRepository.findById(articleId).orElseThrow()
        return ArticleResponse.of(article)
    }

    @Transactional
    fun delete(articleId: Long) {
        val article = articleRepository.findById(articleId).orElseThrow()
        articleRepository.delete(article)
        boardArticleCountRepository.decrease(article.boardId)
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse {
        return ArticlePageResponse(
            articles = articleRepository.findAll(
                boardId = boardId,
                offset = (page - 1) * pageSize,
                limit = pageSize
            ).map {
                ArticleResponse.of(it)
            },
            articleCount = articleRepository.count(
                boardId = boardId,
                limit = PageLimitCalculator.calculatePageLimit(
                    page = page,
                    pageSize = pageSize,
                    movablePageCount = 10
                )
            )
        )
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        pageSize: Long,
        lastArticleId: Long?
    ): List<ArticleResponse> {
        return if (lastArticleId == null) {
            articleRepository.findAllInfiniteScroll(
                boardId = boardId,
                limit = pageSize
            )
        } else {
            articleRepository.findAllInfiniteScroll(
                boardId = boardId,
                limit = pageSize,
                lastArticleId = lastArticleId
            )
        }.map {
            ArticleResponse.of(it)
        }
    }

    fun count(boardId: Long): Long {
        return boardArticleCountRepository.findById(boardId)
            .getOrNull()?.articleCount ?: 0
    }
}