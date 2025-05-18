package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.eventhandler.EventHandler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater(
    private val hotArticleListRepository: HotArticleListRepository,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) {

    companion object {
        const val HOT_ARTICLE_COUNT = 10L
        val HOT_ARTICLE_TTL: Duration = Duration.ofDays(10)
    }

    fun update(event: Event<EventPayload>, handler: EventHandler<EventPayload>) {
        val articleId = handler.findArticleId(event)
        val createdAt = articleCreatedTimeRepository.read(articleId)

        if (!isCreatedArticleToday(createdAt)) {
            return
        }

        handler.handle(event)

        val score = hotArticleScoreCalculator.calculate(articleId)
        hotArticleListRepository.add(
            articleId = articleId,
            time = createdAt!!,
            score = score.toDouble(),
            limit = HOT_ARTICLE_COUNT,
            ttl = HOT_ARTICLE_TTL
        )
    }

    private fun isCreatedArticleToday(createdAt: LocalDateTime?): Boolean {
        return createdAt != null && createdAt.toLocalDate().equals(LocalDate.now())
    }
}