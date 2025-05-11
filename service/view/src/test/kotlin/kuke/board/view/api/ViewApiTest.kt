package kuke.board.view.api

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@SpringBootTest
class ViewApiTest(
) {

    val restClient = RestClient.create("http://localhost:9003")

    @Test
    fun viewTest() {

        val executorService: ExecutorService = Executors.newFixedThreadPool(100)

        val articleId = 3L
        val userId = 1L

        runBlocking(executorService.asCoroutineDispatcher()) {
            buildList {
                repeat(10000) {
                    add(async {
                        restClient.post()
                            .uri("/v1/article-views/articles/${articleId}/users/${userId}")
                            .retrieve()
                            .body<Long>()
                    })
                }
            }.awaitAll()
        }

        val count = restClient.get()
            .uri("/v1/article-views/articles/${articleId}/count")
            .retrieve()
            .body<Long>()

        println("count=$count")
    }
}