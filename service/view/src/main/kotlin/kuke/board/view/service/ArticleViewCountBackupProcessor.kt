package kuke.board.view.service

import jakarta.transaction.Transactional
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.ArticleViewedEventPayload
import kuke.board.common.outboxmessagerelay.OutboxEventPublisher
import kuke.board.view.entity.ArticleViewCount
import kuke.board.view.repository.ArticleViewCountBackupRepository
import org.springframework.stereotype.Component

@Component
class ArticleViewCountBackupProcessor(
    val articleViewCountBackupRepository: ArticleViewCountBackupRepository,
    val outboxEventPublisher: OutboxEventPublisher,
) {
    @Transactional
    fun backup(
        articleId: Long,
        viewCount: Long,
    ) {
        val result = articleViewCountBackupRepository.updateViewCount(
            articleId,
            viewCount
        )
        if (result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                .ifPresentOrElse(
                    { it -> {} },
                    {
                        articleViewCountBackupRepository.save(
                            ArticleViewCount(
                                articleId = articleId,
                                viewCount = viewCount
                            )
                        )
                    }
                )
        }

        outboxEventPublisher.publish(
            eventType = EventType.ARTICLE_VIEWED,
            payload = ArticleViewedEventPayload(
                articleId = articleId,
                articleViewCount = viewCount
            ),
            shardKey = articleId
        )
    }
}
