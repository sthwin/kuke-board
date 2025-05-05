package kuke.board.comment.controller

import kuke.board.comment.service.CommentService
import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    val commentService: CommentService
) {

    @GetMapping("/v1/comments/{commentId}")
    fun read(
        @PathVariable commentId: Long
    ): CommentResponse {
        return commentService.read(commentId)
    }

    @PostMapping("/v1/comments")
    fun create(@RequestBody request: CommentCreateRequest): CommentResponse {
        return commentService.create(request)
    }

    @DeleteMapping("/v1/comments/{commentId}")
    fun delete(@PathVariable commentId: Long) {
        commentService.delete(commentId)
    }

}