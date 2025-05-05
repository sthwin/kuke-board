package kuke.board.article.controller

import kuke.board.article.service.ArticleService
import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticlePageResponse
import kuke.board.article.service.response.ArticleResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
data class ArticleController(
    val articleService: ArticleService
) {

    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable articleId: Long) = articleService.read(articleId)

    @GetMapping("/v1/articles")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long
    ): ArticlePageResponse = articleService.readAll(
        boardId = boardId,
        page = page,
        pageSize = pageSize
    )

    @GetMapping("/v1/articles/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam("lastArticleId") lastArticleId: Long?
    ): List<ArticleResponse> = articleService.readAllInfiniteScroll(
        boardId = boardId,
        pageSize = pageSize,
        lastArticleId = lastArticleId
    )

    @PostMapping("/v1/articles")
    fun create(@RequestBody request: ArticleCreateRequest) = articleService.create(request)

    @PutMapping("/v1/articles/{articleId}")
    fun update(@PathVariable articleId: Long, @RequestBody request: ArticleUpdateRequest) =
        articleService.update(articleId, request)

    @DeleteMapping("/v1/articles/{articleId}")
    fun delete(@PathVariable articleId: Long) = articleService.delete(articleId)
}
