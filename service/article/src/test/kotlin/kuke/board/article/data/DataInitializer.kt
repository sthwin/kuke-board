package kuke.board.article.data

import jakarta.persistence.EntityManager
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kuke.board.article.entity.Article
import kuke.board.common.dataserializer.Snowflake
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import kotlin.test.Test
import kotlin.time.measureTime

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DataInitializer(
    val entityManager: EntityManager,
    val transactionTemplate: TransactionTemplate
) {

    companion object {
        const val BULK_INSERT_SIZE = 2000
        const val EXECUTE_COUNT = 6000
    }

    val snowflake = Snowflake()
    val countDownLatch = CountDownLatch(EXECUTE_COUNT)

    @Test
    fun initialize() {
        measureTime {
            runBlocking {
                repeat(EXECUTE_COUNT) {
                    async {
                        insert()
                        countDownLatch.countDown()
                        println("latch.getCount() = ${countDownLatch.count}")
                    }.await()
                }
            }
        }
    }

    fun insert() {
        transactionTemplate.executeWithoutResult { status ->
            for (i in 0 until BULK_INSERT_SIZE) {
                val article = Article(
                    articleId = snowflake.nextId(),
                    title = "title $i",
                    content = "content $i",
                    boardId = 1,
                    writerId = 1
                )
                entityManager.persist(article)
            }
        }
    }
}