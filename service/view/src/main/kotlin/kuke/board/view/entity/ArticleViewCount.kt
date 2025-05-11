package kuke.board.view.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "article_view_count")
@Entity
data class ArticleViewCount(
    @Id
    val articleId: Long,
    val viewCount: Long
) {

}