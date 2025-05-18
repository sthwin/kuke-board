package kuke.board.like.service

import kuke.board.common.dataserializer.Snowflake
import kuke.board.like.entity.ArticleLike
import kuke.board.like.entity.ArticleLikeCount
import kuke.board.like.repository.ArticleLikeCountRepository
import kuke.board.like.repository.ArticleLikeRepository
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class ArticleLikeService(
    val articleLikeRepository: ArticleLikeRepository,
    val articleLikeCountRepository: ArticleLikeCountRepository
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
    fun likePessimistickLock1(articleId: Long, userId: Long) {
        try {
            articleLikeRepository.save(
                ArticleLike(
                    articleLikeId = snowflake.nextId(),
                    articleId = articleId,
                    userId = userId
                )
            )
            val result = articleLikeCountRepository.increase(articleId)
            if (result == 0) {
                // 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화한다.
                // 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수 도 있다.
                articleLikeCountRepository.save(
                    ArticleLikeCount.init(
                        articleId = articleId,
                        likeCount = 1L
                    )
                )
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Transactional
    fun unlikePessimistickLock1(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(
            articleId = articleId,
            userId = userId
        ).ifPresent {
            articleLikeRepository.delete(it)
            articleLikeCountRepository.decrease(articleId)
        }
    }


    /**
     * select ... for update
     */
    @Transactional
    fun likePessimistickLock2(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )

        val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
            .orElse(
                ArticleLikeCount.init(
                    articleId = articleId,
                    likeCount = 0L
                )
            )

        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount)
    }

    @Transactional
    fun unlikePessimistickLock2(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(
            articleId = articleId,
            userId = userId
        ).ifPresent {
            articleLikeRepository.delete(it)
            articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseThrow()
                .decrease()
        }
    }

    @Transactional
    fun likeOptimistickLock(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )
        val articleLikeCount = articleLikeCountRepository.findById(articleId).orElse(
            ArticleLikeCount.init(
                articleId = articleId,
                likeCount = 0L
            )
        )
        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount)
    }

    @Transactional
    fun unlikeOptimistickLock(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(
            articleId = articleId,
            userId = userId
        ).ifPresent {
            articleLikeRepository.delete(it)
            articleLikeCountRepository.findById(articleId)
                .orElseThrow()
                .decrease()
        }
    }

    fun count(articleId: Long): Long {
        return articleLikeCountRepository.findById(articleId)
            .getOrNull()?.likeCount ?: 0
    }

}