package kuke.board.article.api

import kuke.board.article.service.request.ArticleCreateRequest
import kuke.board.article.service.request.ArticleUpdateRequest
import kuke.board.article.service.response.ArticlePageResponse
import kuke.board.article.service.response.ArticleResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.test.Test

class ArticleApiTest {

    val restClient = RestClient.create("http://localhost:9000")


    @Test
    fun createTest() {
        val articleResponse = create(
            ArticleCreateRequest(
                title = "hi",
                content = "my content",
                boardId = 1,
                writerId = 1
            )
        )
        print(articleResponse)
    }

    @Test
    fun readTest() {
        val articleResponse = read(176911644303466496)
        print(articleResponse)
    }

    @Test
    fun updateTest() {
        update(176911644303466496)
        read(176911644303466496)
    }

    @Test
    fun deleteTest() {
        restClient.delete()
            .uri("/v1/articles/176911644303466496")
            .retrieve()
            .body<Unit>()
    }

    fun create(articleCreateRequest: ArticleCreateRequest): ArticleResponse {
        return restClient.post()
            .uri("/v1/articles")
            .body(articleCreateRequest)
            .retrieve()
            .body<ArticleResponse>()!!
    }

    fun read(articleId: Long): ArticleResponse {
        return restClient.get()
            .uri("/v1/articles/$articleId")
            .retrieve()
            .body<ArticleResponse>()!!
            .also {
                println(it)
            }
    }

    fun update(articleId: Long) = restClient.put()
        .uri("/v1/articles/${articleId}")
        .body(
            ArticleUpdateRequest(
                title = "hi 3",
                content = "my content 3"
            )
        )
        .retrieve()
        .body<ArticleResponse>()

    @Test
    fun readAllTest() {
        val response = restClient.get()
            .uri("/v1/articles?boardId=1&page=50000&pageSize=30")
            .retrieve()
            .body<ArticlePageResponse>()

        println("article response=$response")
        println(("response article count=" + response?.articleCount))
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val result = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
            .retrieve()
            .body<List<ArticleResponse>>()

        println("result count=${result?.size}")
        result?.forEach {
            println(it)
        }

        val result2 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=${result?.last()?.articleId}")
            .retrieve()
            .body<List<ArticleResponse>>()
        println("result2 count=${result2?.size}")
        result2?.forEach {
            println(it)
        }
    }

    @Test
    fun countTest() {
        val response = create(
            ArticleCreateRequest(
                title = "hi",
                content = "my content",
                boardId = 2,
                writerId = 1
            )
        )

        val count1 = restClient.get()
            .uri("/v1/articles/boards/${response.boardId}/count")
            .retrieve()
            .body<Long>()
        println("count1=$count1")

        restClient.delete()
            .uri("/v1/articles/${response.articleId}")
            .retrieve()
            .body<Unit>()

        val count2 = restClient.get()
            .uri("/v1/articles/boards/${response.boardId}/count")
            .retrieve()
            .body<Long>()
        println("count1=$count2")
    }
}