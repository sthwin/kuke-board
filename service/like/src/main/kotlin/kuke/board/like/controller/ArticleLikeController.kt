package kuke.board.like.controller

import kuke.board.like.service.ArticleLikeService
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ArticleLikeController(
    val articleLikeService: ArticleLikeService
) {

    @GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun read(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ): ArticleLikeResponse {
        return articleLikeService.read(articleId, userId)
    }

    @GetMapping("/v1/article-likes/articles/{articleId}/count")
    fun count(
        @PathVariable articleId: Long,
    ): Long {
        return articleLikeService.count(articleId)
    }

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    fun likePessimisticLock1(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.likePessimistickLock1(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    fun unlikePessimisticLock1(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.unlikePessimistickLock1(articleId, userId)
    }

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    fun likePessimisticLock2(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.likePessimistickLock2(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    fun unlikePessimisticLock2(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.unlikePessimistickLock2(articleId, userId)
    }

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    fun likeOptimisticLock(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.likePessimistickLock1(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    fun unlikeOptimisticLock(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ) {
        articleLikeService.unlikePessimistickLock1(articleId, userId)
    }
}