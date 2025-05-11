package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import org.springframework.stereotype.Service

@Service
class ArticleViewService(
    val articleViewCountRepository: ArticleViewCountRepository,
    val articleViewCountBackupProcessor: ArticleViewCountBackupProcessor
) {

    companion object {
        const val BACK_UP_BACH_SIZE = 100
    }

    fun increase(articleId: Long): Long {
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