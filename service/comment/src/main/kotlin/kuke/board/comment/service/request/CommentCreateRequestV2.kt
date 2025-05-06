package kuke.board.comment.service.request

import jakarta.persistence.Id

data class CommentCreateRequestV2(
    val articleId: Long,
    val content: String,
    val parentPath: String?,
    val writerId: Long
)