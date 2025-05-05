package kuke.board.comment.repository

import kuke.board.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    @Query(
        nativeQuery = true,
        value = """
            select count(*)
            from (
                select comment_id
                from comment
                where article_id = :articleId and parent_comment_id = :parentCommentId
                limit :limit
            ) t
        """
    )
    fun countBy(
        @Param("articleId") articleId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long
    ): Long
}