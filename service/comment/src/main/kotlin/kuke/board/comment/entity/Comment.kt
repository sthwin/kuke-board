package kuke.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kuke.board.comment.service.response.CommentResponse
import java.time.LocalDateTime

@Table(name = "comment")
@Entity
data class Comment(
    @Id
    var commentId: Long,
    var content: String,
    var parentCommentId: Long,
    var articleId: Long, // shard key
    var writerId: Long,
    var deleted: Boolean,
    var createdAt: LocalDateTime
) {

    val root: Boolean
        get() = parentCommentId == commentId

    companion object {
        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long?,
            articleId: Long,
            writerId: Long
        ): Comment {
            return Comment(
                commentId = commentId,
                content = content,
                parentCommentId = parentCommentId ?: commentId,
                articleId = articleId,
                writerId = writerId,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        }
    }

    fun delete() {
        this.deleted = true
    }

    fun toCommentResponse(): CommentResponse {
        return CommentResponse(
            commentId = this.commentId,
            content = this.content,
            parentCommentId = this.parentCommentId,
            articleId = this.articleId,
            writerId = this.writerId,
            deleted = this.deleted,
            createdAt = this.createdAt
        )
    }
}