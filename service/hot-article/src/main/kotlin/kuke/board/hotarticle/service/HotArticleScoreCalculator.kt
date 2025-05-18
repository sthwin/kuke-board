package kuke.board.hotarticle.service

import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import org.springframework.stereotype.Component

@Component
class HotArticleScoreCalculator(
    val articleLikeCountRepository: ArticleLikeCountRepository,
    val articleViewCountRepository: ArticleViewCountRepository,
    val articleCommentCountRepository: ArticleCommentCountRepository,
) {

    companion object {
        private const val ARTICLE_LIKE_COUNT_WEIGHT = 3L
        private const val ARTICLE_COMMENT_COUNT_WEIGHT = 2L
        private const val ARTICLE_VIEW_COUNT_WEIGHT = 1L
    }

    fun calculate(articleId: Long): Long {
        val likeCount = articleLikeCountRepository.read(articleId)
        val commentCount = articleCommentCountRepository.read(articleId)
        val viewCount = articleViewCountRepository.read(articleId)

        return (likeCount * ARTICLE_LIKE_COUNT_WEIGHT) +
                (commentCount * ARTICLE_COMMENT_COUNT_WEIGHT) +
                (viewCount * ARTICLE_VIEW_COUNT_WEIGHT)
    }
}