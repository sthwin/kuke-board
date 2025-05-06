package kuke.board.comment.service.response

import java.time.LocalDateTime

data class CommentResponseV2(
    val commentId: Long,
    val content: String,
    val path: String,
    val articleId: Long, // shard key
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime
) {

}