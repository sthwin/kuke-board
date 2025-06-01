package kuke.board.articleread.controller

import kuke.board.articleread.service.ArticleReadService
import kuke.board.articleread.service.response.ArticleReadPageResponse
import kuke.board.articleread.service.response.ArticleReadResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ArticleReadController(
    val articleReadService: ArticleReadService
) {

    @GetMapping("/v1/articles/{articleId}")
    fun read(
        @PathVariable articleId: Long
    ): ArticleReadResponse {
        return articleReadService.read(articleId)
    }

    @GetMapping("/v1/articles")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): ArticleReadPageResponse {
        return articleReadService.readAll(boardId, page, pageSize)
    }

    @GetMapping("/v1/articles/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam("lastArticleId") lastArticleId: Long?
    ): List<ArticleReadResponse> {
        return articleReadService.readAllInfiniteScroll(
            boardId = boardId,
            pageSize = pageSize,
            lastArticleId = lastArticleId
        )
    }
}