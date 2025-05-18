package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.hotarticle.client.ArticleClient
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class HotArticleServiceTest {

    @InjectMocks
    lateinit var hotArticleService: HotArticleService

    @Mock
    lateinit var eventHandlers: List<EventHandler<EventPayload>>

    @Mock
    lateinit var hotArticleScoreUpdater: HotArticleScoreUpdater

    @Mock
    lateinit var hotArticleListRepository: HotArticleListRepository

    @Mock
    lateinit var articleClient: ArticleClient

    @Test
    fun handleEventIfEventHandlerNotFoundTest() {
        // given
        val event = mock(Event::class.java) as Event<EventPayload>
        val eventHandler = mock(EventHandler::class.java) as EventHandler<EventPayload>
        given(eventHandler.supports(event)).willReturn(false)

        // 실제 리스트를 생성하고 모킹된 이벤트 핸들러를 추가
        val eventHandlers = listOf(eventHandler)
        val hotArticleService = HotArticleService(
            articleClient = articleClient,
            eventHandlers = eventHandlers,
            hotArticleScoreUpdater = hotArticleScoreUpdater,
            hotArticleListRepository = hotArticleListRepository,
        )

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(eventHandler, never()).handle(event)
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler)
    }

    @Test
    fun handleEventIfArticleCreatedEventTest() {
        // given
        val event = mock(Event::class.java) as Event<EventPayload>
        given(event.type).willReturn(EventType.ARTICLE_CREATED)

        val eventHandler = mock(EventHandler::class.java) as EventHandler<EventPayload>
        given(eventHandler.supports(event)).willReturn(true)

        // 실제 리스트를 생성하고 모킹된 이벤트 핸들러를 추가
        val eventHandlers = listOf(eventHandler)
        val hotArticleService = HotArticleService(
            articleClient = articleClient,
            eventHandlers = eventHandlers,
            hotArticleScoreUpdater = hotArticleScoreUpdater,
            hotArticleListRepository = hotArticleListRepository,
        )

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(eventHandler).handle(event)
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler)
    }

    @Test
    fun handleEventIfArticleDeletedEventTest() {
        // given
        val event = mock(Event::class.java) as Event<EventPayload>
        given(event.type).willReturn(EventType.ARTICLE_DELETED)

        val eventHandler = mock(EventHandler::class.java) as EventHandler<EventPayload>
        given(eventHandler.supports(event)).willReturn(true)

        // 실제 리스트를 생성하고 모킹된 이벤트 핸들러를 추가
        val eventHandlers = listOf(eventHandler)
        val hotArticleService = HotArticleService(
            articleClient = articleClient,
            eventHandlers = eventHandlers,
            hotArticleScoreUpdater = hotArticleScoreUpdater,
            hotArticleListRepository = hotArticleListRepository,
        )

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(eventHandler).handle(event)
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler)
    }

    @Test
    fun handleEventIfScoreUpdatableEventTest() {
        // given
        val event = mock(Event::class.java) as Event<EventPayload>
        val randomEventTypes = listOf(
            EventType.ARTICLE_LIKED,
            EventType.ARTICLE_UNLIKED,
            EventType.ARTICLE_VIEWED,
            EventType.COMMENT_CREATED,
            EventType.COMMENT_DELETED
        )
        given(event.type).willReturn(randomEventTypes.random())

        val eventHandler = mock(EventHandler::class.java) as EventHandler<EventPayload>
        given(eventHandler.supports(event)).willReturn(true)

        // 실제 리스트를 생성하고 모킹된 이벤트 핸들러를 추가
        val eventHandlers = listOf(eventHandler)
        val hotArticleService = HotArticleService(
            articleClient = articleClient,
            eventHandlers = eventHandlers,
            hotArticleScoreUpdater = hotArticleScoreUpdater,
            hotArticleListRepository = hotArticleListRepository,
        )

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(eventHandler, never()).handle(event)
        verify(hotArticleScoreUpdater).update(event, eventHandler)
    }
}