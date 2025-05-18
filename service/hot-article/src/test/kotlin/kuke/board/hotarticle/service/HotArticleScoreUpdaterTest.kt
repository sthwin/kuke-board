package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class HotArticleScoreUpdaterTest {

    @InjectMocks
    lateinit var hotArticleScoreUpdater: HotArticleScoreUpdater

    @Mock
    lateinit var hotArticleListRepository: HotArticleListRepository

    @Mock
    lateinit var hotArticleScoreCalculator: HotArticleScoreCalculator

    @Mock
    lateinit var articleCreatedTimeRepository: ArticleCreatedTimeRepository

    @Test
    fun updateIfArticleNotCreatedTodayTest() {

        // given
        val articleId = 1L
        val event = mock(Event::class.java) as Event<EventPayload>
        val eventHandler = mock(EventHandler::class.java) as EventHandler<EventPayload>

        given(eventHandler.findArticleId(event)).willReturn(articleId)

        val createdAt = LocalDateTime.now().minusDays(1)
        given(articleCreatedTimeRepository.read(articleId)).willReturn(createdAt)

        // when
        hotArticleScoreUpdater.update(
            event = event,
            handler = eventHandler
        )

        // then
        verify(eventHandler, never()).handle(event)
        verify(hotArticleListRepository, never()).add(
            articleId = articleId,
            time = LocalDateTime.now(),
            score = 0.0,
            limit = 1L,
            ttl = Duration.ofSeconds(3)
        )
    }

    @Test
    fun updateTest() {

        // given
        val articleId = 1L
        val event = mock(Event::class.java) as Event<EventPayload>
        val eventHandler = mock(EventHandler::class.java) as EventHandler<EventPayload>

        given(eventHandler.findArticleId(event)).willReturn(articleId)

        val createdAt = LocalDateTime.now()
        given(articleCreatedTimeRepository.read(articleId)).willReturn(createdAt)

        val score = 100L
        given(hotArticleScoreCalculator.calculate(articleId)).willReturn(score)

        // when
        hotArticleScoreUpdater.update(
            event = event,
            handler = eventHandler
        )

        // then
        verify(eventHandler).handle(event)
        verify(hotArticleListRepository).add(
            articleId = articleId,
            time = createdAt,
            score = score.toDouble(),
            limit = HotArticleScoreUpdater.HOT_ARTICLE_COUNT,
            ttl = HotArticleScoreUpdater.HOT_ARTICLE_TTL
        )
    }
}