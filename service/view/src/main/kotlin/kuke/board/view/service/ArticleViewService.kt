package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import kuke.board.view.repository.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleViewService(
    val articleViewCountRepository: ArticleViewCountRepository,
    val articleViewCountBackupProcessor: ArticleViewCountBackupProcessor,
    val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository
) {

    companion object {
        const val BACK_UP_BACH_SIZE = 100
        val TTL = Duration.ofMinutes(10)
    }

    fun increase(articleId: Long): Long {
        if (!articleViewDistributedLockRepository.lock(articleId, 1, TTL)) {
            return count(articleId)
        }
        val count = articleViewCountRepository.increase(articleId)
        if (count % BACK_UP_BACH_SIZE == 0L) {
            articleViewCountBackupProcessor.backup(articleId, count)
        }
        return count
    }

    fun count(articleId: Long): Long {
        return articleViewCountRepository.read(articleId)
    }

}