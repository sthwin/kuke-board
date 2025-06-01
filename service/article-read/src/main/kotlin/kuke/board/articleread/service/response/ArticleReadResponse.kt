package kuke.board.articleread.service.response

import kuke.board.articleread.repository.ArticleQueryModel
import java.time.LocalDateTime

data class ArticleReadResponse(
    val articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var articleCommentCount: Long,
    var articleLikeCount: Long,
    var articleViewCount: Long
) {
    companion object {
        fun of(articleQueryModel: ArticleQueryModel, viewCount: Long) = ArticleReadResponse(
            articleId = articleQueryModel.articleId,
            title = articleQueryModel.title,
            content = articleQueryModel.content,
            boardId = articleQueryModel.boardId,
            writerId = articleQueryModel.writerId,
            createdAt = articleQueryModel.createdAt,
            modifiedAt = articleQueryModel.modifiedAt,
            articleCommentCount = articleQueryModel.articleCommentCount,
            articleLikeCount = articleQueryModel.articleLikeCount,
            articleViewCount = viewCount
        )
    }
}
