package kuke.board.like.controller

import kuke.board.like.service.ArticleLikeService
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ArticleLikeController(
    val articleLikeService: ArticleLikeService
) {

    @GetMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    fun read(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ): ArticleLikeResponse {
        return articleLikeService.read(articleId, userId)
    }

    @PostMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    fun like(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.like(articleId, userId)
    }


}