package kuke.board.articleread.client

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ViewClientTest(
    val viewClient: ViewClient
) {

    @Test
    fun count() {
        viewClient.count(1L) // 로그 출력
        viewClient.count(1L) // 로그 미출력
        viewClient.count(1L) // 로그 미출력

        TimeUnit.SECONDS.sleep(3)
        viewClient.count(1L) // 로그 출력
    }

    @Test
    fun readCachableMultiThreadTest() {
        val executorService: ExecutorService = Executors.newFixedThreadPool(5)

        viewClient.count(1L) // init cache

        runBlocking() {

            repeat(5) {
                buildList {
                    repeat(5) {
                        add(async(executorService.asCoroutineDispatcher()) {
                            viewClient.count(1L)
                        })
                    }
                }.awaitAll()
                delay(2000)
                println("===== cache expired =====")
            }
        }
    }
}
