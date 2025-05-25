package kuke.board.hotarticle.data

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.random.RandomGenerator

class DataInitializer {

    val articleServiceClient = RestClient.create("http://localhost:9000")
    val commentServiceClient = RestClient.create("http://localhost:9001")
    val likeServiceClient = RestClient.create("http://localhost:9002")
    val viewServiceClient = RestClient.create("http://localhost:9003")

    @Test
    fun init() {
        repeat(30) {
            val articleId = createArticle()
            val commentCount = RandomGenerator.getDefault().nextLong(10)
            val likeCount = RandomGenerator.getDefault().nextLong(10)
            val viewCount = RandomGenerator.getDefault().nextLong(200)

            createComments(articleId, commentCount)
            createLikes(articleId, likeCount)
            createViews(articleId, viewCount)
        }
    }

    private fun createViews(articleId: Long, viewCount: Long) {
        repeat(viewCount.toInt()) {
            viewServiceClient.post()
                .uri("/v1/article-views/articles/$articleId/users/$viewCount")
                .retrieve()
                .body<Any>()
        }
    }

    private fun createLikes(articleId: Long, likeCount: Long) {
        repeat(likeCount.toInt()) {
            likeServiceClient.post()
                .uri("/v1/article-likes/articles/$articleId/users/$likeCount/pessimistic-lock-1")
                .retrieve()
                .body<Any>()
        }
    }

    private fun createComments(articleId: Long, commentCount: Long) {
        repeat(commentCount.toInt()) {
            val result = commentServiceClient.post()
                .uri("/v2/comments")
                .body(CommentCreateRequestV2(articleId, "comment", 1L))
                .retrieve()
                .body<Any>()

            println(result)
        }
    }

    fun createArticle(): Long {
        return articleServiceClient.post()
            .uri("/v1/articles")
            .body(
                ArticleCreateRequest(
                    title = "title",
                    content = "content",
                    writerId = 1L,
                    boardId = 1L,
                )
            )
            .retrieve()
            .body<ArticleResponse>()!!.articleId

    }

    data class CommentCreateRequestV2(
        val articleId: Long,
        val content: String,
        val writerId: Long,
    )

    data class ArticleCreateRequest(
        val title: String,
        val content: String,
        val writerId: Long,
        val boardId: Long,
    )

    data class ArticleResponse(
        val articleId: Long,
    )
}