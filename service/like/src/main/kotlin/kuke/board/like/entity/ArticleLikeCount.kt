package kuke.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "article_like_count")
data class ArticleLikeCount(
    @Id
    val articleId: Long,
    var likeCount: Long,
    @Version
    val version: Long,
) {

    companion object {
        fun init(
            articleId: Long,
            likeCount: Long
        ): ArticleLikeCount {
            return ArticleLikeCount(
                articleId = articleId,
                likeCount = likeCount,
                version = 0
            )
        }
    }

    fun increase() {
        likeCount++
    }

    fun decrease() {
        likeCount--
    }
}