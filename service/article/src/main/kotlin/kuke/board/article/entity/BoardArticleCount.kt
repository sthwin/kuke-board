package kuke.board.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "board_article_count")
data class BoardArticleCount(
    @Id
    val boardId: Long,
    val articleCount: Long
) {

    companion object {
        fun init(boardId: Long, articleCount: Long): BoardArticleCount {
            return BoardArticleCount(
                boardId = boardId,
                articleCount = articleCount
            )
        }
    }
}