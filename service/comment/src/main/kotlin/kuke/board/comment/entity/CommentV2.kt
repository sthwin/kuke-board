package kuke.board.comment.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kuke.board.comment.service.response.CommentResponseV2
import java.time.LocalDateTime

@Table(name = "comment_v2")
@Entity
data class CommentV2(
    @Id
    var commentId: Long,
    var content: String,
    var articleId: Long, // shard key
    var writerId: Long,
    @Embedded
    @Column(name = "path")
    val commentPath: CommentPath,
    var deleted: Boolean,
    var createdAt: LocalDateTime
) {

    companion object {
        fun of(
            commentId: Long,
            content: String,
            articleId: Long,
            writerId: Long,
            commentPath: CommentPath,
        ): CommentV2 {
            return CommentV2(
                commentId = commentId,
                content = content,
                articleId = articleId,
                writerId = writerId,
                commentPath = commentPath,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        }
    }

    val root: Boolean
        get() = commentPath.root

    fun delete() {
        this.deleted = true
    }

    fun toCommentResponseV2(): CommentResponseV2 {
        return CommentResponseV2(
            commentId = this.commentId,
            content = this.content,
            path = this.commentPath.path,
            articleId = this.articleId,
            writerId = this.writerId,
            deleted = this.deleted,
            createdAt = this.createdAt,
        )
    }

}