package kuke.board.hotarticle.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class HotArticleListRepositoryTest(
    val hotArticleListRepository: HotArticleListRepository
) {

    @Test
    fun addTest() {

        // given
        val time = LocalDateTime.of(2024, 7, 23, 0, 0)
        val limit = 3L // 상위 3건만 남김

        // when
        hotArticleListRepository.add(
            articleId = 1L,
            time = time,
            score = 2.0,
            limit = limit,
            ttl = Duration.ofSeconds(3)
        )
        hotArticleListRepository.add(
            articleId = 2L,
            time = time,
            score = 3.0,
            limit = limit,
            ttl = Duration.ofSeconds(3)
        )
        hotArticleListRepository.add(
            articleId = 3L,
            time = time,
            score = 1.0,
            limit = limit,
            ttl = Duration.ofSeconds(3)
        )
        hotArticleListRepository.add(
            articleId = 4L,
            time = time,
            score = 5.0,
            limit = limit,
            ttl = Duration.ofSeconds(3)
        )
        hotArticleListRepository.add(
            articleId = 5L,
            time = time,
            score = 4.0,
            limit = limit,
            ttl = Duration.ofSeconds(3)
        )

        // then
        val articleIds = hotArticleListRepository.readAll(time)

        assertThat(articleIds).hasSize(limit.toInt())
        assertThat(articleIds[0]).isEqualTo(4L)
        assertThat(articleIds[1]).isEqualTo(5L)
        assertThat(articleIds[2]).isEqualTo(2L)

        TimeUnit.SECONDS.sleep(5)

        assertThat(hotArticleListRepository.readAll(time)).isEmpty()
    }
}