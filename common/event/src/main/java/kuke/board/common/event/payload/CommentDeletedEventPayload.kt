package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload

class CommentDeletedEventPayload(
    val commentId: Long,
    val content: String,
    val path: String,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: String,
    val articleCommentCount: Long,
) : EventPayload {
}