package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.hotarticle.client.ArticleClient
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import kuke.board.hotarticle.service.response.HotArticleResponse
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HotArticleService(
    val articleClient: ArticleClient,
    val eventHandlers: List<EventHandler<EventPayload>>,
    val hotArticleScoreUpdater: HotArticleScoreUpdater,
    val hotArticleListRepository: HotArticleListRepository,
) {

    fun handleEvent(event: Event<EventPayload>) {
        val handler = findEventHandler(event)
        if (handler == null) {
            return
        }

        if (isArticleCreatedOrDeleted(event)) {
            handler.handle(event)
        } else {
            hotArticleScoreUpdater.update(event, handler)
        }
    }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return when (event.type) {
            EventType.ARTICLE_CREATED -> true
            EventType.ARTICLE_DELETED -> true
            else -> false
        }
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<EventPayload>? {
        return eventHandlers.firstOrNull { it.supports(event) } as EventHandler<EventPayload>?
    }

    fun readAll(dateTime: LocalDateTime): List<HotArticleResponse> {
        return hotArticleListRepository.readAll(dateTime)
            .map {
                HotArticleResponse.of(
                    articleResponse = articleClient.read(it)
                )
            }
    }
}