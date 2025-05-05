package kuke.board.article.repository

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import kotlin.test.assertEquals

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ArticleRepositoryTest(
    val articleRepository: ArticleRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Test
    fun findAllTest() {
        val articles = articleRepository.findAll(
            boardId = 1,
            offset = 1499970,
            limit = 30
        )

        log.info("Found ${articles.size} articles")
    }

    @Test
    fun countTest() {
        val count = articleRepository.count(
            boardId = 1,
            limit = 10000
        )
        log.info("count = {}", count)
    }

    @Test
    fun findAllInfiniteScrollTest() {
        val articles = articleRepository.findAllInfiniteScroll(
            boardId = 1,
            limit = 30,
        )
        log.info("last articleId ${articles.last().articleId}")

        val results = articleRepository.findAllInfiniteScroll(
            boardId = 1,
            limit = 30,
            lastArticleId = articles.last().articleId
        )
        log.info("first articleId ${results.first().articleId}")
    }
}
