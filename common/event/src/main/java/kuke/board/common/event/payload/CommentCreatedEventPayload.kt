package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

class CommentCreatedEventPayload(
    val commentId: Long,
    val content: String,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
    val articleCommentCount: Long,
) : EventPayload {
}