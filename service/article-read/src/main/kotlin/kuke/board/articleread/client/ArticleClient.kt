package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.kuke-board-article-service.url}")
    val articleServiceUrl: String,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var restClient: RestClient

    @PostConstruct
    fun init() {
        restClient = RestClient.create(articleServiceUrl)
    }

    fun read(articleId: Long): ArticleResponse? {
        return runCatching {
            restClient.get()
                .uri("/v1/articles/${articleId}")
                .retrieve()
                .body<ArticleResponse>()!!
        }.onFailure {
            log.error("[ArticleClient.read] articleId=$articleId", it)
        }.getOrNull()
    }

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): ArticlePageResponse {
        return runCatching {
            restClient.get()
                .uri("/v1/articles?boardId=$boardId&page=$page&pageSize=$pageSize")
                .retrieve()
                .body<ArticlePageResponse>()!!
        }.getOrElse {
            log.error("[ArticleClient.readAll] boardId=$boardId, page=$page, pageSize=$pageSize", it)
            ArticlePageResponse.EMPTY
        }
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        pageSize: Long,
        lastArticleId: Long?
    ): List<ArticleResponse> {
        return runCatching {
            val uri = if (lastArticleId == null) {
                "/v1/articles/infinite-scroll?boardId=$boardId&pageSize=$pageSize"
            } else {
                "/v1/articles/infinite-scroll?boardId=$boardId&pageSize=$pageSize&lastArticleId=$lastArticleId"
            }
            return restClient.get()
                .uri(uri)
                .retrieve()
                .body<List<ArticleResponse>>()!!
        }.getOrElse {
            log.error(
                "[ArticleClient.readAllInfiniteScroll] boardId=$boardId, pageSize=$pageSize, lastArticleId=$lastArticleId",
                it
            )
            emptyList()
        }

    }

    fun count(boardId: Long): Long {
        return runCatching {
            restClient.get()
                .uri("/v1/articles/boards/$boardId/count")
                .retrieve()
                .body<Long>()!!
        }.getOrElse {
            log.error("[ArticleClient.count] boardId=$boardId", it)
            0L
        }
    }

    data class ArticlePageResponse(
        val articles: List<ArticleResponse>,
        val articleCount: Long,
    ) {
        companion object {
            val EMPTY = ArticlePageResponse(
                articles = emptyList(),
                articleCount = 0L,
            )
        }
    }

    data class ArticleResponse(
        val articleId: Long,
        val title: String,
        val content: String,
        val boardId: Long,
        val writerId: Long,
        val createdAt: LocalDateTime,
        val modifiedAt: LocalDateTime,
    )
}