package kuke.board.like.service

import kuke.board.common.snowflake.Snowflake
import kuke.board.like.entity.ArticleLike
import kuke.board.like.repository.ArticleLikeRepository
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    val articleLikeRepository: ArticleLikeRepository
) {
    val snowflake = Snowflake()

    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        return articleLikeRepository.findByArticleIdAndUserId(
            articleId = articleId,
            userId = userId
        ).map {
            ArticleLikeResponse.of(it)
        }.orElseThrow()

    }

    @Transactional
    fun like(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )
    }

    @Transactional
    fun unlike(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(
            articleId = articleId,
            userId = userId
        ).ifPresent {
            articleLikeRepository.delete(it)
        }
    }
}