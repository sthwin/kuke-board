package kuke.board.comment.service.response

import kuke.board.comment.entity.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long, // shard key
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime
) {

}