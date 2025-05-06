package kuke.board.comment.controller

import kuke.board.comment.service.CommentServiceV2
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentResponseV2
import org.springframework.web.bind.annotation.*

@RestController
class CommentControllerV2(
    val commentServiceV2: CommentServiceV2
) {

    @GetMapping("/v2/comments/{commentId}")
    fun read(
        @PathVariable commentId: Long
    ): CommentResponseV2 {
        return commentServiceV2.read(commentId)
    }

    @PostMapping("/v2/comments")
    fun create(@RequestBody request: CommentCreateRequestV2): CommentResponseV2 {
        return commentServiceV2.create(request)
    }

    @DeleteMapping("/v2/comments/{commentId}")
    fun delete(@PathVariable commentId: Long) {
        commentServiceV2.delete(commentId)
    }
}