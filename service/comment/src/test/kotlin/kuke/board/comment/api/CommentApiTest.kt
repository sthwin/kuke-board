package kuke.board.comment.api

import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.test.Test

@SpringBootTest
class CommentApiTest {

    val restClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = createComment(
            CommentCreateRequest(
                articleId = 1,
                content = "my comment1",
                parentCommentId = null,
                writerId = 1,
            )
        )
        val response2 = createComment(
            CommentCreateRequest(
                articleId = 1,
                content = "my comment2",
                parentCommentId = response1.commentId,
                writerId = 1,
            )
        )
        val response3 = createComment(
            CommentCreateRequest(
                articleId = 1,
                content = "my comment3",
                parentCommentId = response1.commentId,
                writerId = 1,
            )
        )

        println("commentId=${response1.commentId}")
        println("\tcommentId=${response2.commentId}")
        println("\tcommentId=${response3.commentId}")

//        commentId=177612890797182976
//        commentId=177612891296305152
//        commentId=177612891350831104

    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v1/comments/177612890797182976")
            .retrieve()
            .body<CommentResponse>()

        println(response)
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v1/comments/177612891350831104")
            .retrieve()
            .body<Unit>()
    }

    fun createComment(commentCreateRequest: CommentCreateRequest): CommentResponse {
        return restClient.post()
            .uri("/v1/comments")
            .body(commentCreateRequest)
            .retrieve()
            .body<CommentResponse>()!!
    }
}