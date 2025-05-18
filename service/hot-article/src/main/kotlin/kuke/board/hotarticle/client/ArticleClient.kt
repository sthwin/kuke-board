package kuke.board.hotarticle.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.kuke-board-article-service.url}")
    val articleServiceUrl: String
) {
    val restClient = RestClient.create(articleServiceUrl)

    fun read(articleId: Long): ArticleResponse {
        return restClient.get()
            .uri("/v1/articles/${articleId}")
            .retrieve()
            .body<ArticleResponse>()!!
    }

    data class ArticleResponse(
        val articleId: Long,
        val title: String,
        val createdAt: LocalDateTime,
    )
}