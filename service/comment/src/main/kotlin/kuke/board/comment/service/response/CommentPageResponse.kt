package kuke.board.comment.service.response

data class CommentPageResponse(
    val comments: List<CommentResponse>,
    val commentCount: Long
)
