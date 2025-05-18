package kuke.board.comment.data

import jakarta.persistence.EntityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kuke.board.comment.entity.Comment
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
            runBlocking(Dispatchers.IO) {
                List(EXECUTE_COUNT) {
                    async {
                        insert()
                        countDownLatch.countDown()
                        println("latch.getCount() = ${countDownLatch.count}")
                    }
                }.awaitAll()
            }
        }
    }

    fun insert() {
        transactionTemplate.executeWithoutResult { status ->
            var prev: Comment? = null
            for (i in 0 until BULK_INSERT_SIZE) {
                val comment = Comment.create(
                    commentId = snowflake.nextId(),
                    content = "content",
                    parentCommentId = if (i % 2 == 0) null else prev?.commentId,
                    articleId = 1,
                    writerId = 1
                )
                prev = comment
                entityManager.persist(comment)
            }
        }
    }
}