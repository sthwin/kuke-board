package kuke.board.hotarticle.service.response

import kuke.board.hotarticle.client.ArticleClient
import java.time.LocalDateTime

data class HotArticleResponse(
    val articleId: Long,
    val title: String,
    val createdAt: LocalDateTime,
) {

    companion object {
        fun of(articleResponse: ArticleClient.ArticleResponse): HotArticleResponse {
            return HotArticleResponse(
                articleId = articleResponse.articleId,
                title = articleResponse.title,
                createdAt = articleResponse.createdAt,
            )
        }
    }
}