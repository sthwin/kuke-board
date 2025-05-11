package kuke.board.view.controller

import kuke.board.view.service.ArticleViewService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleViewController(
    val articleViewService: ArticleViewService
) {

    @PostMapping("/v1/article-views/articles/{articleId}/users/{userId}")
    fun increase(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ): Long {
        return articleViewService.increase(articleId)
    }

    @GetMapping("/v1/article-views/articles/{articleId}/count")
    fun count(
        @PathVariable articleId: Long,
    ): Long {
        return articleViewService.count(articleId)
    }
}