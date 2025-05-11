package kuke.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Table(name = "article_comment_count")
@Entity
data class ArticleCommentCount(
    @Id
    val articleId: Long,
    val commentCount: Long
) {

    companion object {
        fun init(articleId: Long, commentCount: Long): ArticleCommentCount {
            return ArticleCommentCount(
                articleId = articleId,
                commentCount = commentCount,
            )
        }
    }
}