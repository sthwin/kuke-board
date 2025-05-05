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

    @Query(
        nativeQuery = true,
        value = """
            select 
                    comment.comment_id,
                    comment.content,
                    comment.article_id,
                    comment.parent_comment_id,
                    comment.writer_id,
                    comment.deleted,
                    comment.created_at
            from (
                select comment_id
                from comment
                where article_id = :articleId
                order by parent_comment_id asc, comment_id asc
                limit :limit offset :offset
            ) t left join comment on t.comment_id = comment.comment_id 
        """
    )
    fun findAll(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long
    ): List<Comment>

    @Query(
        nativeQuery = true,
        value = """
            select count(*)
            from (
                select comment_id
                from comment
                where article_id = :articleId
                limit :limit
            ) t
        """
    )
    fun count(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long
    ): Long

    /**
     * 첫 번째 페이지 조회
     */
    @Query(
        nativeQuery = true,
        value = """
            select 
                    comment.comment_id,
                    comment.content,
                    comment.article_id,
                    comment.parent_comment_id,
                    comment.writer_id,
                    comment.deleted,
                    comment.created_at
                from comment
                where article_id = :articleId
                order by parent_comment_id asc, comment_id asc
                limit :limit
        """
    )
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long
    ): List<Comment>

    /**
     * 첫 번째 이후 페이지 조회
     */
    @Query(
        nativeQuery = true,
        value = """
            select 
                    comment.comment_id,
                    comment.content,
                    comment.article_id,
                    comment.parent_comment_id,
                    comment.writer_id,
                    comment.deleted,
                    comment.created_at
            from (
                select comment_id
                from comment
                where   article_id = :articleId and
                        (parent_comment_id > :lastParentCommentId 
                        or (parent_comment_id = :lastParentCommentId and comment_id > :lastCommentId)) 
                order by parent_comment_id asc, comment_id asc
                limit :limit
            ) t left join comment on t.comment_id = comment.comment_id 
        """
    )
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("lastParentCommentId") lastParentCommentId: Long,
        @Param("lastCommentId") lastCommentId: Long,
        @Param("limit") limit: Long
    ): List<Comment>
}