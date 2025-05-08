package kuke.board.like.repository

import kuke.board.like.entity.ArticleLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ArticleLikeRepository : JpaRepository<ArticleLike, Long> {
    fun findByArticleIdAndUserId(articleId: Long, userId: Long): Optional<ArticleLike>
}