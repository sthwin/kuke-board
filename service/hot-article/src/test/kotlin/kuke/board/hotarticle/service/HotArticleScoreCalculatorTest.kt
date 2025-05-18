package kuke.board.hotarticle.service

import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.random.RandomGenerator

@ExtendWith(MockitoExtension::class)
class HotArticleScoreCalculatorTest {
    @InjectMocks
    private lateinit var hotArticleScoreCalculator: HotArticleScoreCalculator

    @Mock
    private lateinit var articleLikeCountRepository: ArticleLikeCountRepository

    @Mock
    private lateinit var articleViewCountRepository: ArticleViewCountRepository

    @Mock
    private lateinit var articleCommentCountRepository: ArticleCommentCountRepository

    @Test
    fun calculateTest() {
        // given
        val articleId = 1L
        val likeCount = RandomGenerator.getDefault().nextLong(100)
        val viewCount = RandomGenerator.getDefault().nextLong(100)
        val commentCount = RandomGenerator.getDefault().nextLong(100)

        given(articleLikeCountRepository.read(articleId)).willReturn(likeCount)
        given(articleViewCountRepository.read(articleId)).willReturn(viewCount)
        given(articleCommentCountRepository.read(articleId)).willReturn(commentCount)

        // when
        val score = hotArticleScoreCalculator.calculate(articleId)
        println(score)

        // then
        assertEquals(
            (likeCount * 3) +
                    (commentCount * 2) +
                    (viewCount * 1), score
        )
    }
}