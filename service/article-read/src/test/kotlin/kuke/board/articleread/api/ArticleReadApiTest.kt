package kuke.board.articleread.api

import kuke.board.articleread.service.response.ArticleReadPageResponse
import kuke.board.articleread.service.response.ArticleReadResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.client.body


class ArticleReadApiTest {
    val articleReadRestClient = RestClient.create("http://localhost:9005")
    val articleRestClient = RestClient.create("http://localhost:9000")

    @Test
    fun readTest() {
        val articleId = 187102435446362112L
        articleReadRestClient.get().uri("/v1/articles/$articleId")
            .retrieve()
            .body<ArticleReadResponse>()
            .also {
                println(it)
            }
    }

    @Test
    fun readAllTest() {
        val response1 = articleReadRestClient.get().uri("/v1/articles?boardId=1&page=3000&pageSize=5")
            .retrieve()
            .body<ArticleReadPageResponse>()
            .also {
                println("respponse1.getArticleCount=${it?.articleCount}")
                it?.articles?.forEach {
                    println("articleId=${it.articleId}")
                }
            }

        val response2 = articleRestClient.get().uri("/v1/articles?boardId=1&page=3000&pageSize=5")
            .retrieve()
            .body<ArticleReadPageResponse>()
            .also {
                println("respponse2.getArticleCount=${it?.articleCount}")
                it?.articles?.forEach {
                    println("articleId=${it.articleId}")
                }
            }
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val response1 = articleReadRestClient.get()
//            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5") // 첫 페이지 조회
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=187218560213585920")
            .retrieve()
            .body<List<ArticleReadResponse>>()!!
            .also { it ->
                println("respponse1.getArticleCount=${it.size}")
                it.forEach {
                    println("articleId=${it.articleId}")
                }
            }

        val response2 = articleRestClient.get()
//            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5") // 첫 페이지 조회
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=187218560213585920")
            .retrieve()
            .body<List<ArticleReadResponse>>()!!
            .also { it ->
                println("response2.getArticleCount=${it.size}")
                it.forEach {
                    println("articleId=${it.articleId}")
                }
            }
    }
}