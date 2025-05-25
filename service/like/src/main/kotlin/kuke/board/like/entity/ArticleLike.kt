package kuke.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article_like")
@Entity
data class ArticleLike(
    @Id
    val articleLikeId: Long,
    val articleId: Long, // shard key
    val userId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
}