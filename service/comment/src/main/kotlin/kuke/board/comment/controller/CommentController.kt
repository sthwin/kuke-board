package kuke.board.comment.controller

import kuke.board.comment.service.CommentService
import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/v1/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long
    ): CommentPageResponse {
        return commentService.readAll(
            articleId = articleId,
            page = page,
            pageSize = pageSize
        )
    }

    // /v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=177684112655491075&lastCommentId=177684112655491075

    @GetMapping("/v1/comments/infinite-scroll")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("lastParentCommentId") lastParentCommentId: Long?,
        @RequestParam("lastCommentId") lastCommentId: Long?,
        @RequestParam("pageSize") pageSize: Long
    ): List<CommentResponse> {
        return commentService.readAll(
            articleId = articleId,
            lastParentCommentId = lastParentCommentId,
            lastCommentId = lastCommentId,
            pageSize = pageSize
        )
    }

}