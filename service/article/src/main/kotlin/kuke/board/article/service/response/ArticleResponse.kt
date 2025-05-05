package kuke.board.article.service.response

import kuke.board.article.entity.Article
import java.time.LocalDateTime

data class ArticleResponse(
    var articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
) {
    companion object {
        fun of(article: Article): ArticleResponse {
            return ArticleResponse(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
            )
        }
    }
}