package kuke.board.articleread.repository

import kuke.board.articleread.client.ArticleClient
import kuke.board.common.event.payload.*
import java.time.LocalDateTime

data class ArticleQueryModel(
    val articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var articleCommentCount: Long,
    var articleLikeCount: Long,
    var articleViewCount: Long? = null
) {
    companion object {
        fun of(payload: ArticleCreatedEventPayload) = ArticleQueryModel(
            articleId = payload.articleId,
            title = payload.title,
            content = payload.content,
            boardId = payload.boardId,
            writerId = payload.writerId,
            createdAt = payload.createdAt,
            modifiedAt = payload.modifiedAt,
            articleCommentCount = 0L,
            articleLikeCount = 0L,
            articleViewCount = 0L
        )

        fun of(
            articleResponse: ArticleClient.ArticleResponse,
            commentCount: Long,
            likeCount: Long,
        ) = ArticleQueryModel(
            articleId = articleResponse.articleId,
            title = articleResponse.title,
            content = articleResponse.content,
            boardId = articleResponse.boardId,
            writerId = articleResponse.writerId,
            createdAt = articleResponse.createdAt,
            modifiedAt = articleResponse.modifiedAt,
            articleCommentCount = commentCount,
            articleLikeCount = likeCount,
        )
    }

    fun updateBy(payload: CommentCreatedEventPayload) {
        articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: CommentDeletedEventPayload) {
        articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: ArticleLikedEventPayload) {
        articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUnlikedEventPayload) {
        articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUpdatedEventPayload) {
        title = payload.title
        content = payload.content
        boardId = payload.boardId
        writerId = payload.writerId
        createdAt = payload.createdAt
        modifiedAt = payload.modifiedAt
    }
}
