package kuke.board.like.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kuke.board.like.service.response.ArticleLikeResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.time.measureTime


@SpringBootTest
class LikeApiTest {
    val restClient = RestClient.create("http://localhost:9002")

    @Test
    fun likeAndUnlikeTest() {
        val articleId = 9999L

        like(articleId, 1L, "pessimistic-lock-1")
        like(articleId, 2L, "pessimistic-lock-1")
        like(articleId, 3L, "pessimistic-lock-1")

        val response1 = read(articleId, 1L)
        val response2 = read(articleId, 2L)
        val response3 = read(articleId, 3L)
        println(response1)
        println(response2)
        println(response3)

        unlike(articleId, 1L, "pessimistic-lock-1")
        unlike(articleId, 2L, "pessimistic-lock-1")
        unlike(articleId, 3L, "pessimistic-lock-1")
    }

    @Test
    fun likePerfomanceTest() {
        val executorService = Executors.newFixedThreadPool(100)
        likePerfomanceTest(
            executorService,
            1111L,
            "pessimistic-lock-1"
        )
        likePerfomanceTest(
            executorService,
            2222L,
            "pessimistic-lock-2"
        )
        likePerfomanceTest(
            executorService,
            3333L,
            "optimistic-lock"
        )

    }

    fun likePerfomanceTest(
        executorService: ExecutorService,
        articleId: Long,
        lockType: String
    ) {
        measureTime {
            println("start $lockType")
            like(articleId, 0L, lockType)
            val dispatcher = executorService.asCoroutineDispatcher()

            runBlocking(dispatcher) {
                buildList {
                    for (i in 1..3000) {
                        add(async {
                            println("current thread: ${Thread.currentThread().name}")
                            like(articleId, i.toLong(), lockType)
                        })
                    }
                }.awaitAll()
            }
        }.also {
            println("$lockType-걸린시간: ${it.inWholeMilliseconds}ms")
            println("end $lockType")

            val result = restClient.get()
                .uri("/v1/article-likes/articles/$articleId/count")
                .retrieve()
                .body<Int>()
            println("like count: $result")
        }
    }

    fun like(
        articleId: Long,
        userId: Long,
        lockType: String,
    ) {
        restClient.post()
            .uri("/v1/article-likes/articles/$articleId/users/$userId/" + lockType)
            .retrieve()
            .body<Unit>()
    }

    fun unlike(
        articleId: Long,
        userId: Long,
        lockType: String,
    ) {
        restClient.delete()
            .uri("/v1/article-likes/articles/$articleId/users/$userId/" + lockType)
            .retrieve()
            .body<Unit>()
    }

    fun read(
        articleId: Long,
        userId: Long
    ): ArticleLikeResponse {
        return restClient.get()
            .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
            .body<ArticleLikeResponse>()!!
    }
}