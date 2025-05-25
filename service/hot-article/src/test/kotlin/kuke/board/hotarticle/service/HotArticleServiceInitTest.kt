package kuke.board.hotarticle.service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class HotArticleServiceInitTest(
    val hotArticleService: HotArticleService
) {

    @Test
    fun init() {
        println(hotArticleService.eventHandlers.size)
    }

}