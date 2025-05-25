package kuke.board.hotarticle.api

import kuke.board.hotarticle.service.response.HotArticleResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.client.body


class HotArticleApiTest {

    val restClient = RestClient.create("http://localhost:9004")

    @Test
    fun readAllTest() {
        val resultList = restClient.get()
            .uri("/v1/hot-article/articles/date/20250525")
            .retrieve()
            .body<List<HotArticleResponse>>()
        resultList?.forEach {
            println(it)
        }
    }
}