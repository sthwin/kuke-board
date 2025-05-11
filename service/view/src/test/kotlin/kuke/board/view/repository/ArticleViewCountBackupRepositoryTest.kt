package kuke.board.view.repository

import jakarta.persistence.EntityManager
import kuke.board.view.entity.ArticleViewCount
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ArticleViewCountBackupRepositoryTest(
    val articleViewCountBackupRepository: ArticleViewCountBackupRepository,
    val entityManager: EntityManager
) {

    @Test
    @Transactional
    fun updateViewCountTest() {
        // given
        articleViewCountBackupRepository.save(
            ArticleViewCount(
                articleId = 1L,
                viewCount = 0L
            )
        )
        entityManager.flush()
        entityManager.clear()

        // when
        val result1 = articleViewCountBackupRepository.updateViewCount(1L, 100L)
        val result2 = articleViewCountBackupRepository.updateViewCount(1L, 300L)
        val result3 = articleViewCountBackupRepository.updateViewCount(1L, 200L)

        // then
        assert(result1 == 1)
        assert(result2 == 1)
        assert(result3 == 0)

        val viewCount = articleViewCountBackupRepository.findById(1L).get().viewCount
        assert(viewCount == 300L)
    }
}