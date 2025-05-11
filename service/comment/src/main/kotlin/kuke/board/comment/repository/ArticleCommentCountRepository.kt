package kuke.board.comment.repository

import kuke.board.comment.entity.ArticleCommentCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleCommentCountRepository : JpaRepository<ArticleCommentCount, Long> {

    @Query(
        nativeQuery = true,
        value = """
            update article_comment_count
            set comment_count = comment_count + 1
            where article_id = :articleId
        """
    )
    @Modifying
    fun increase(@Param("articleId") articleId: Long): Int

    @Query(
        nativeQuery = true,
        value = """
            update article_comment_count
            set comment_count = comment_count - 1
            where article_id = :articleId
        """
    )
    @Modifying
    fun decrease(@Param("articleId") articleId: Long): Int
}