package kuke.board.comment.data

import jakarta.persistence.EntityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kuke.board.comment.entity.CommentPath
import kuke.board.comment.entity.CommentV2
import kuke.board.common.dataserializer.Snowflake
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import kotlin.test.Test
import kotlin.time.measureTime

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DataInitializerV2(
    val entityManager: EntityManager,
    val transactionTemplate: TransactionTemplate
) {

    companion object {
        const val BULK_INSERT_SIZE = 2000
        const val EXECUTE_COUNT = 6000
        const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        const val DEPTH_CHUNK_SIZE = 5
    }

    val snowflake = Snowflake()
    val countDownLatch = CountDownLatch(EXECUTE_COUNT)

    @Test
    fun initialize() {
        measureTime {
            runBlocking(Dispatchers.IO) {
                (0 until EXECUTE_COUNT).map {
                    async {
                        val start = it * BULK_INSERT_SIZE
                        val end = start + BULK_INSERT_SIZE
                        insert(start, end)
                        countDownLatch.countDown()
                        println("latch.getCount() = ${countDownLatch.count}")
                    }
                }.awaitAll()
            }
        }
    }

    fun insert(start: Int, end: Int) {
        transactionTemplate.executeWithoutResult { status ->
            for (i in start until end) {
                val comment = CommentV2(
                    commentId = snowflake.nextId(),
                    content = "content",
                    articleId = 1,
                    writerId = 1,
                    deleted = false,
                    createdAt = LocalDateTime.now(),
                    commentPath = toPath(i)
                )
                entityManager.persist(comment)
            }
        }
    }

    private fun toPath(value: Int): CommentPath {
        var initValue = value
        var path = ""
        for (i in 0 until DEPTH_CHUNK_SIZE) {
            path = CHARSET[initValue % CHARSET.length] + path
            initValue /= CHARSET.length
        }
        return CommentPath(path)
    }
}