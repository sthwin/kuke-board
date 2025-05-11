package kuke.board.comment.controller

import kuke.board.comment.service.CommentServiceV2
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponseV2
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

    @GetMapping("/v2/comments")
    fun readAll(
        @RequestParam articleId: Long,
        @RequestParam page: Long,
        @RequestParam pageSize: Long,
    ): CommentPageResponseV2 {
        return commentServiceV2.readAll(
            articleId = articleId,
            page = page,
            pageSize = pageSize,
        )
    }

    @GetMapping("/v2/comments/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam articleId: Long,
        @RequestParam lastPath: String?,
        @RequestParam pageSize: Long,
    ): List<CommentResponseV2> {
        return commentServiceV2.readAllInfiniteScroll(
            articleId = articleId,
            lastPath = lastPath,
            pageSize = pageSize,
        )
    }

    @GetMapping("/v2/comments/articles/{articleId}/count")
    fun count(
        @PathVariable articleId: Long
    ): Long {
        return commentServiceV2.count(articleId)
    }
}