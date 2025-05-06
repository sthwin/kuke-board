package kuke.board.comment.api

import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import kuke.board.comment.service.response.CommentResponseV2
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.test.Test

@SpringBootTest
class CommentApiV2Test {

    val restClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = create(
            CommentCreateRequestV2(
                articleId = 1,
                content = "my comment1",
                parentPath = null,
                writerId = 1
            )
        )


        val response2 = create(
            CommentCreateRequestV2(
                articleId = 1,
                content = "my comment2",
                parentPath = response1.path,
                writerId = 1
            )
        )


        val response3 = create(
            CommentCreateRequestV2(
                articleId = 1,
                content = "my comment3",
                parentPath = response2.path,
                writerId = 1
            )
        )
        println("${response1.path}(${response1.commentId})")
        println("${response2.path}(${response2.commentId})")
        println("${response3.path}(${response3.commentId}")

        // 출력결과
        // 00002(178068274548592640)
        // 0000200000(178068274779279360)
        // 000020000000000(178068274850582528
    }

    fun create(commentCreateRequest: CommentCreateRequestV2): CommentResponseV2 {
        return restClient.post()
            .uri("/v2/comments")
            .body(commentCreateRequest)
            .retrieve()
            .body<CommentResponseV2>()!!
    }


    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v2/comments/{commentId}", 178068274850582528)
            .retrieve()
            .body<CommentResponseV2>()
        println(response)
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v2/comments/{commentId}", 178068274850582528)
            .retrieve()
            .body<Unit>()
    }
}